package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderProduct;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.PaymentMethod;
import com.colors.ecommerce.backend.domain.model.ProductVariant;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.model.StockReservation;
import com.colors.ecommerce.backend.domain.model.StockReservationStatus;
import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.domain.model.Category;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.domain.model.DeliveryType;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExchangeItemRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExchangeRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ReturnItemRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ReturnRequest;
import com.colors.ecommerce.backend.infrastucture.rest.dto.TimelineEventDto;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.domain.port.IStockMovementRepository;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public class OrderService {
    private final IOrderRepository IOrderRepository;
    private final IProductVariantRepository productVariantRepository;
    private final IProductRepository productRepository;
    private final CategoryService categoryService;
    private final IStockReservationRepository stockReservationRepository;
    private final IStockMovementRepository stockMovementRepository;
    private final com.colors.ecommerce.backend.domain.port.IShipmentRepository shipmentRepository;

    public OrderService(IOrderRepository iOrderRepository,
                        IProductVariantRepository productVariantRepository,
                        IProductRepository productRepository,
                        CategoryService categoryService,
                        IStockReservationRepository stockReservationRepository,
                        IStockMovementRepository stockMovementRepository,
                        com.colors.ecommerce.backend.domain.port.IShipmentRepository shipmentRepository) {
        this.IOrderRepository = iOrderRepository;
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.stockReservationRepository = stockReservationRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public Order save(Order order) {
        System.out.println("Guardando orden: " + order);
        if (order.getSaleChannel() == null) {
            order.setSaleChannel(SaleChannel.ONLINE);
        }
        if (order.getOrderState() == null) {
            if (order.getSaleChannel() == SaleChannel.OFFLINE) {
                order.setOrderState(OrderState.COMPLETED);
            } else {
                order.setOrderState(OrderState.PENDING);
            }
        }
        if (order.getPaymentMethod() == null) {
            order.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
        }
        if (order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            order.setOrderNumber(generateOrderNumber(order.getSaleChannel()));
        }
        if (order.getDeliveryType() == null) {
            order.setDeliveryType(DeliveryType.IMMEDIATE);
        }
        if (order.getOrderProducts() != null) {
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                if (orderProduct.getProductVariantId() == null) {
                    throw new RuntimeException("Product variant is required");
                }
                ProductVariant variant = productVariantRepository.findById(orderProduct.getProductVariantId());
                if (variant == null) {
                    throw new RuntimeException("Variant not found: " + orderProduct.getProductVariantId());
                }
                Product product = productRepository.findById(variant.getProductId());
                if (product == null) {
                    throw new RuntimeException("Product not found for variant: " + orderProduct.getProductVariantId());
                }
                if (order.getSaleChannel() == SaleChannel.ONLINE) {
                    if (Boolean.FALSE.equals(product.getSellOnline()) || Boolean.FALSE.equals(variant.getSellOnline())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no disponible online");
                    }
                }
                applyDeliverySnapshot(orderProduct, order, variant, product);
                applyOrderProductPrice(order, orderProduct, variant, product);
            }
        }
        applyOrderDeliveryAggregate(order);
        order.setTotal(order.getTotalOrderPrice());
        Order saved = this.IOrderRepository.save(order);
        if (saved.getOrderState() == OrderState.PENDING) {
            reserveStock(saved);
        } else if (saved.getOrderState() == OrderState.COMPLETED) {
            consumeReservations(saved, "admin");
            saved.setPaidAt(LocalDateTime.now());
            saved = this.IOrderRepository.save(saved);
        }
        return saved;
    }
    public Iterable<Order> findAll() {
        return this.IOrderRepository.findAll();
    }
    public Iterable<Order> findByUserId(Integer userId) {
        return this.IOrderRepository.findByUserId(userId);
    }
    @Transactional
    public Order updateStateById(Integer id, OrderState state) {
        return updateStateById(id, state, "admin");
    }

    @Transactional
    public Order updateStateById(Integer id, OrderState state, String createdBy) {
        Order existingOrder = this.IOrderRepository.findById(id);
        if (existingOrder.getOrderState() == OrderState.COMPLETED) {
            return existingOrder;
        }

        existingOrder.setOrderState(state);
        if (state == OrderState.COMPLETED) {
            consumeReservations(existingOrder, createdBy);
            existingOrder.setPaidAt(LocalDateTime.now());
        } else if (state == OrderState.CANCELLED) {
            releaseReservations(existingOrder);
        }
        return this.IOrderRepository.save(existingOrder);
    }
    public Order findById(Integer id) {
        return this.IOrderRepository.findById(id);
    }

    public Iterable<StockMovement> getMovementsByOrder(Integer orderId) {
        return stockMovementRepository.findByOrderId(orderId);
    }

    public java.util.List<TimelineEventDto> buildTimeline(Integer orderId) {
        Order order = this.IOrderRepository.findById(orderId);
        java.util.List<TimelineEventDto> events = new java.util.ArrayList<>();
        if (order.getDateCreated() != null) {
            events.add(createTimeline("ORDER_CREATED", "Orden creada (PENDING)", order.getDateCreated(), "sistema"));
        }
        if (order.getOrderState() == OrderState.COMPLETED) {
            java.time.LocalDateTime paidAt = order.getPaidAt() != null ? order.getPaidAt() : order.getDateCreated();
            if (paidAt != null) {
                events.add(createTimeline("PAYMENT_CONFIRMED", "Pago confirmado (COMPLETED)", paidAt, "sistema"));
            }
        }

        Iterable<StockMovement> movements = stockMovementRepository.findByOrderId(orderId);
        for (StockMovement movement : movements) {
            if (movement.getCreatedAt() == null) {
                continue;
            }
            String actor = movement.getCreatedBy() == null ? "admin" : movement.getCreatedBy();
            if (movement.getReason() == StockMovementReason.SALE_ONLINE
                    || movement.getReason() == StockMovementReason.SALE_WHOLESALE
                    || movement.getReason() == StockMovementReason.SALE_OFFLINE
                    || movement.getReason() == StockMovementReason.SALE) {
                events.add(createTimeline("STOCK_OUT", "Stock descontado", movement.getCreatedAt(), actor));
            }
            if (movement.getReason() == StockMovementReason.RETURN) {
                events.add(createTimeline("RETURN", "Devolución registrada", movement.getCreatedAt(), actor));
            }
            if (movement.getReason() == StockMovementReason.EXCHANGE_IN) {
                events.add(createTimeline("EXCHANGE_IN", "Cambio de prenda (ingreso)", movement.getCreatedAt(), actor));
            }
            if (movement.getReason() == StockMovementReason.EXCHANGE_OUT) {
                events.add(createTimeline("EXCHANGE_OUT", "Cambio de prenda (egreso)", movement.getCreatedAt(), actor));
            }
        }

        com.colors.ecommerce.backend.domain.model.Shipment shipment = shipmentRepository.findByOrderId(orderId);
        if (shipment != null) {
            if (shipment.getCreatedAt() != null) {
                events.add(createTimeline("SHIPMENT_CREATED", "Envío cargado", shipment.getCreatedAt(), shipment.getCreatedBy()));
            }
            if (shipment.getStatus() == com.colors.ecommerce.backend.domain.model.ShipmentStatus.SHIPPED && shipment.getUpdatedAt() != null) {
                events.add(createTimeline("SHIPPED", "Enviado", shipment.getUpdatedAt(), shipment.getUpdatedBy()));
            }
            if (shipment.getStatus() == com.colors.ecommerce.backend.domain.model.ShipmentStatus.DELIVERED && shipment.getUpdatedAt() != null) {
                events.add(createTimeline("DELIVERED", "Entregado", shipment.getUpdatedAt(), shipment.getUpdatedBy()));
            }
        }

        events.sort(java.util.Comparator.comparing(TimelineEventDto::getTimestamp));
        return events;
    }

    @Transactional
    public Order processReturn(ReturnRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new RuntimeException("Orden requerida");
        }
        Order order = this.IOrderRepository.findById(request.getOrderId());
        if (order.getOrderState() != OrderState.COMPLETED) {
            throw new RuntimeException("Solo se permiten devoluciones en órdenes COMPLETED");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Items requeridos");
        }
        java.time.LocalDateTime now = LocalDateTime.now();
        java.math.BigDecimal refundTotal = java.math.BigDecimal.ZERO;
        java.util.Map<Integer, Integer> alreadyReturned = getReturnedQtyByVariant(order.getId());

        for (ReturnItemRequest item : request.getItems()) {
            validateReturnItem(item);
            OrderProduct orderProduct = findOrderProduct(order, item.getVariantId());
            if (orderProduct == null) {
                throw new RuntimeException("La variante " + item.getVariantId() + " no pertenece a la orden");
            }
            int orderedQty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
            int returnedQty = alreadyReturned.getOrDefault(item.getVariantId(), 0);
            int availableToReturn = orderedQty - returnedQty;
            if (item.getQty() > availableToReturn) {
                throw new RuntimeException("Cantidad a devolver supera lo disponible para la variante " + item.getVariantId());
            }

            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getVariantId());
            Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            variant.setStockCurrent(currentStock + item.getQty());
            productVariantRepository.save(variant);

            StockMovement movement = new StockMovement();
            movement.setVariantId(item.getVariantId());
            movement.setType(StockMovementType.IN);
            movement.setQty(item.getQty());
            movement.setReason(StockMovementReason.RETURN);
            movement.setOrderId(order.getId());
            movement.setSaleChannel(order.getSaleChannel());
            movement.setUnitPrice(orderProduct.getPrice());
            movement.setNote(formatReasonNote(item.getReason(), item.getNote()));
            movement.setCreatedAt(now);
            movement.setCreatedBy(resolveCreatedBy(request.getCreatedBy()));
            stockMovementRepository.save(movement);

            if (orderProduct.getPrice() != null) {
                refundTotal = refundTotal.add(orderProduct.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQty())));
            }
        }

        applyAdjustmentTotals(order, refundTotal.negate());
        return this.IOrderRepository.save(order);
    }

    @Transactional
    public Order processExchange(ExchangeRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new RuntimeException("Orden requerida");
        }
        Order order = this.IOrderRepository.findById(request.getOrderId());
        if (order.getOrderState() != OrderState.COMPLETED) {
            throw new RuntimeException("Solo se permiten cambios en órdenes COMPLETED");
        }
        if ((request.getReturnItems() == null || request.getReturnItems().isEmpty()) &&
            (request.getNewItems() == null || request.getNewItems().isEmpty())) {
            throw new RuntimeException("Debe incluir devoluciones y/o nuevos productos");
        }

        java.time.LocalDateTime now = LocalDateTime.now();
        java.math.BigDecimal returnTotal = java.math.BigDecimal.ZERO;
        java.math.BigDecimal outTotal = java.math.BigDecimal.ZERO;
        java.util.Map<Integer, Integer> alreadyReturned = getReturnedQtyByVariant(order.getId());

        if (request.getReturnItems() != null) {
            for (ReturnItemRequest item : request.getReturnItems()) {
                validateReturnItem(item);
                OrderProduct orderProduct = findOrderProduct(order, item.getVariantId());
                if (orderProduct == null) {
                    throw new RuntimeException("La variante " + item.getVariantId() + " no pertenece a la orden");
                }
                int orderedQty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
                int returnedQty = alreadyReturned.getOrDefault(item.getVariantId(), 0);
                int availableToReturn = orderedQty - returnedQty;
                if (item.getQty() > availableToReturn) {
                    throw new RuntimeException("Cantidad a devolver supera lo disponible para la variante " + item.getVariantId());
                }

                ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getVariantId());
                Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
                variant.setStockCurrent(currentStock + item.getQty());
                productVariantRepository.save(variant);

                StockMovement movement = new StockMovement();
                movement.setVariantId(item.getVariantId());
                movement.setType(StockMovementType.IN);
                movement.setQty(item.getQty());
                movement.setReason(StockMovementReason.EXCHANGE_IN);
                movement.setOrderId(order.getId());
                movement.setSaleChannel(order.getSaleChannel());
                movement.setUnitPrice(orderProduct.getPrice());
                movement.setNote(formatReasonNote(item.getReason(), item.getNote()));
                movement.setCreatedAt(now);
                movement.setCreatedBy(resolveCreatedBy(request.getCreatedBy()));
                stockMovementRepository.save(movement);

                if (orderProduct.getPrice() != null) {
                    returnTotal = returnTotal.add(orderProduct.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQty())));
                }
            }
        }

        if (request.getNewItems() != null) {
            for (ExchangeItemRequest item : request.getNewItems()) {
                if (item == null || item.getVariantId() == null) {
                    throw new RuntimeException("Variante requerida para cambio");
                }
                if (item.getQty() == null || item.getQty() <= 0) {
                    throw new RuntimeException("Cantidad inválida para cambio");
                }
                if (item.getNote() == null || item.getNote().isBlank()) {
                    throw new RuntimeException("Nota obligatoria para cambio");
                }
                ProductVariant variant = productVariantRepository.findByIdForUpdate(item.getVariantId());
                Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
                if (currentStock < item.getQty()) {
                    throw new RuntimeException("Stock insuficiente para cambio en la variante " + item.getVariantId());
                }
                variant.setStockCurrent(currentStock - item.getQty());
                productVariantRepository.save(variant);

                Product product = productRepository.findById(variant.getProductId());
                java.math.BigDecimal unitPrice = resolveExchangeOutPrice(order, item, variant, product);

                StockMovement movement = new StockMovement();
                movement.setVariantId(item.getVariantId());
                movement.setType(StockMovementType.OUT);
                movement.setQty(item.getQty());
                movement.setReason(StockMovementReason.EXCHANGE_OUT);
                movement.setOrderId(order.getId());
                movement.setSaleChannel(order.getSaleChannel());
                movement.setUnitPrice(unitPrice);
                movement.setNote(item.getNote());
                movement.setCreatedAt(now);
                movement.setCreatedBy(resolveCreatedBy(request.getCreatedBy()));
                stockMovementRepository.save(movement);

                if (unitPrice != null) {
                    outTotal = outTotal.add(unitPrice.multiply(java.math.BigDecimal.valueOf(item.getQty())));
                }
            }
        }

        java.math.BigDecimal delta = outTotal.subtract(returnTotal);
        applyAdjustmentTotals(order, delta);
        return this.IOrderRepository.save(order);
    }

    private void reserveStock(Order order) {
        List<StockReservation> existing = stockReservationRepository.findByOrderId(order.getId());
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Integer variantId = orderProduct.getProductVariantId();
            Integer qty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
            if (qty <= 0) {
                continue;
            }
            ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
            Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            Integer reserved = stockReservationRepository.sumActiveReservedQty(variantId, now);
            int available = currentStock - reserved;
            if (available < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para la variante " + variantId);
            }

            StockReservation reservation = new StockReservation();
            reservation.setVariantId(variantId);
            reservation.setOrderId(order.getId());
            reservation.setQty(qty);
            reservation.setCreatedAt(now);
            reservation.setExpiresAt(now.plusMinutes(30));
            reservation.setStatus(StockReservationStatus.ACTIVE);
            stockReservationRepository.save(reservation);
        }
    }

    private void consumeReservations(Order order, String createdBy) {
        List<StockReservation> reservations = stockReservationRepository.findActiveByOrderId(order.getId());
        if (reservations == null || reservations.isEmpty()) {
            // Fallback: si no hay reservas activas, consumimos según productos de la orden
            if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
                throw new RuntimeException("No hay reservas ni productos para la orden " + order.getId());
            }
            LocalDateTime now = LocalDateTime.now();
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                Integer variantId = orderProduct.getProductVariantId();
                Integer qty = orderProduct.getQuantity() == null ? 0 : orderProduct.getQuantity().intValue();
                if (qty <= 0) {
                    continue;
                }
                ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId);
                Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
                if (currentStock < qty) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para confirmar la orden " + order.getId());
                }
                variant.setStockCurrent(currentStock - qty);
                productVariantRepository.save(variant);

                StockMovement movement = new StockMovement();
                movement.setVariantId(variantId);
                movement.setType(StockMovementType.OUT);
                movement.setQty(qty);
                movement.setReason(resolveSaleReason(order.getSaleChannel()));
                movement.setOrderId(order.getId());
                movement.setSaleChannel(order.getSaleChannel());
                movement.setUnitPrice(orderProduct.getPrice());
                movement.setCreatedAt(now);
                movement.setCreatedBy(createdBy);
                stockMovementRepository.save(movement);
            }
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (StockReservation reservation : reservations) {
            ProductVariant variant = productVariantRepository.findByIdForUpdate(reservation.getVariantId());
            Integer currentStock = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            Integer qty = reservation.getQty() == null ? 0 : reservation.getQty();
            if (currentStock < qty) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para confirmar la orden " + order.getId());
            }
            variant.setStockCurrent(currentStock - qty);
            productVariantRepository.save(variant);

            reservation.setStatus(StockReservationStatus.CONSUMED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);

            StockMovement movement = new StockMovement();
            movement.setVariantId(reservation.getVariantId());
            movement.setType(StockMovementType.OUT);
            movement.setQty(qty);
            movement.setReason(resolveSaleReason(order.getSaleChannel()));
            movement.setOrderId(order.getId());
            movement.setSaleChannel(order.getSaleChannel());
            movement.setUnitPrice(findOrderProductPrice(order, reservation.getVariantId()));
            movement.setCreatedAt(now);
            movement.setCreatedBy(createdBy);
            stockMovementRepository.save(movement);
        }
    }

    private void releaseReservations(Order order) {
        List<StockReservation> reservations = stockReservationRepository.findActiveByOrderId(order.getId());
        if (reservations == null || reservations.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (StockReservation reservation : reservations) {
            reservation.setStatus(StockReservationStatus.RELEASED);
            reservation.setReleasedAt(now);
            stockReservationRepository.save(reservation);
        }
    }

    private void applyOrderProductPrice(Order order, OrderProduct orderProduct, ProductVariant variant, Product product) {
        if (order.getSaleChannel() == SaleChannel.OFFLINE) {
            if (orderProduct.getPrice() != null && orderProduct.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
                return;
            }
            orderProduct.setPrice(resolveRetailPrice(variant, product));
            return;
        }

        if (order.getSaleChannel() == SaleChannel.WHOLESALE) {
            if (variant.getPriceWholesale() != null) {
                orderProduct.setPrice(variant.getPriceWholesale());
            } else {
                orderProduct.setPrice(resolveRetailPrice(variant, product));
            }
            return;
        }

        orderProduct.setPrice(resolveRetailPrice(variant, product));
    }

    private java.math.BigDecimal resolveRetailPrice(ProductVariant variant, Product product) {
        if (variant.getPriceRetail() != null) {
            return variant.getPriceRetail();
        }
        if (Boolean.TRUE.equals(product.getPriceOverride())) {
            if (product.getPrice() == null) {
                throw new RuntimeException("Custom price is missing for product " + product.getId());
            }
            return product.getPrice();
        }
        Category category = categoryService.findById(product.getCategoryId());
        if (category == null || category.getPrice() == null) {
            throw new RuntimeException("Category price not set for product " + product.getId());
        }
        return category.getPrice();
    }

    private StockMovementReason resolveSaleReason(SaleChannel channel) {
        if (channel == SaleChannel.WHOLESALE) {
            return StockMovementReason.SALE_WHOLESALE;
        }
        if (channel == SaleChannel.OFFLINE) {
            return StockMovementReason.SALE_OFFLINE;
        }
        return StockMovementReason.SALE_ONLINE;
    }

    private String generateOrderNumber(SaleChannel channel) {
        String prefix = switch (channel) {
            case WHOLESALE -> "MAY";
            case OFFLINE -> "OFF";
            default -> "WEB";
        };
        long count = IOrderRepository.countBySaleChannel(channel);
        long next = count + 1;
        return String.format("%s-%06d", prefix, next);
    }

    private void applyDeliverySnapshot(OrderProduct orderProduct, Order order, ProductVariant variant, Product product) {
        DeliveryType type = variant.getDeliveryType() != null ? variant.getDeliveryType() : product.getDeliveryType();
        if (type == null) {
            type = DeliveryType.IMMEDIATE;
        }
        orderProduct.setDeliveryType(type);

        java.time.LocalDate estimatedDate = variant.getEstimatedDeliveryDate();
        Integer estimatedDays = variant.getEstimatedDeliveryDays();
        String note = variant.getDeliveryNote();
        if (estimatedDate == null) {
            estimatedDate = product.getEstimatedDeliveryDate();
        }
        if (estimatedDays == null) {
            estimatedDays = product.getEstimatedDeliveryDays();
        }
        if (note == null || note.isBlank()) {
            note = product.getDeliveryNote();
        }
        if (estimatedDate == null && estimatedDays != null) {
            java.time.LocalDate baseDate = order.getDateCreated() != null ? order.getDateCreated().toLocalDate() : java.time.LocalDate.now();
            estimatedDate = baseDate.plusDays(estimatedDays);
        }
        orderProduct.setEstimatedDeliveryDate(estimatedDate);
        orderProduct.setDeliveryNote(note);
    }

    private void applyOrderDeliveryAggregate(Order order) {
        if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
            return;
        }
        DeliveryType type = DeliveryType.IMMEDIATE;
        java.time.LocalDate latestDate = null;
        for (OrderProduct item : order.getOrderProducts()) {
            DeliveryType itemType = item.getDeliveryType() == null ? DeliveryType.IMMEDIATE : item.getDeliveryType();
            if (itemType == DeliveryType.DELAYED) {
                type = DeliveryType.DELAYED;
                if (item.getEstimatedDeliveryDate() != null) {
                    if (latestDate == null || item.getEstimatedDeliveryDate().isAfter(latestDate)) {
                        latestDate = item.getEstimatedDeliveryDate();
                    }
                }
            }
        }
        order.setDeliveryType(type);
        order.setEstimatedDeliveryDate(latestDate);
    }

    private java.math.BigDecimal findOrderProductPrice(Order order, Integer variantId) {
        if (order.getOrderProducts() == null) {
            return null;
        }
        for (OrderProduct item : order.getOrderProducts()) {
            if (item.getProductVariantId() != null && item.getProductVariantId().equals(variantId)) {
                return item.getPrice();
            }
        }
        return null;
    }

    private TimelineEventDto createTimeline(String type, String label, java.time.LocalDateTime timestamp, String actor) {
        TimelineEventDto dto = new TimelineEventDto();
        dto.setType(type);
        dto.setLabel(label);
        dto.setTimestamp(timestamp);
        dto.setActor(actor == null || actor.isBlank() ? "sistema" : actor);
        return dto;
    }

    private void validateReturnItem(ReturnItemRequest item) {
        if (item == null || item.getVariantId() == null) {
            throw new RuntimeException("Variante requerida para devolución");
        }
        if (item.getQty() == null || item.getQty() <= 0) {
            throw new RuntimeException("Cantidad inválida para devolución");
        }
        if (item.getReason() == null || item.getReason().isBlank()) {
            throw new RuntimeException("Motivo obligatorio para devolución");
        }
        if (item.getNote() == null || item.getNote().isBlank()) {
            throw new RuntimeException("Nota obligatoria para devolución");
        }
    }

    private String formatReasonNote(String reason, String note) {
        if (reason == null || reason.isBlank()) {
            return note;
        }
        if (note == null || note.isBlank()) {
            return reason;
        }
        return reason + " · " + note;
    }

    private String resolveCreatedBy(String createdBy) {
        return (createdBy == null || createdBy.isBlank()) ? "admin" : createdBy;
    }

    private java.util.Map<Integer, Integer> getReturnedQtyByVariant(Integer orderId) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        Iterable<StockMovement> movements = stockMovementRepository.findByOrderId(orderId);
        for (StockMovement movement : movements) {
            if (movement.getVariantId() == null || movement.getQty() == null) {
                continue;
            }
            if (movement.getReason() == StockMovementReason.RETURN || movement.getReason() == StockMovementReason.EXCHANGE_IN) {
                map.merge(movement.getVariantId(), movement.getQty(), Integer::sum);
            }
        }
        return map;
    }

    private OrderProduct findOrderProduct(Order order, Integer variantId) {
        if (order.getOrderProducts() == null) {
            return null;
        }
        for (OrderProduct item : order.getOrderProducts()) {
            if (item.getProductVariantId() != null && item.getProductVariantId().equals(variantId)) {
                return item;
            }
        }
        return null;
    }

    private void applyAdjustmentTotals(Order order, java.math.BigDecimal delta) {
        if (delta == null || java.math.BigDecimal.ZERO.compareTo(delta) == 0) {
            return;
        }
        java.math.BigDecimal currentTotal = order.getTotal() == null ? order.getTotalOrderPrice() : order.getTotal();
        if (currentTotal == null) {
            currentTotal = java.math.BigDecimal.ZERO;
        }
        order.setTotal(currentTotal.add(delta));
        if (delta.compareTo(java.math.BigDecimal.ZERO) > 0) {
            order.setBalanceDue(delta);
            order.setBalanceCredit(java.math.BigDecimal.ZERO);
        } else {
            order.setBalanceDue(java.math.BigDecimal.ZERO);
            order.setBalanceCredit(delta.abs());
        }
    }

    private java.math.BigDecimal resolveExchangeOutPrice(Order order, ExchangeItemRequest item, ProductVariant variant, Product product) {
        if (order.getSaleChannel() == SaleChannel.OFFLINE) {
            if (item.getUnitPrice() != null && item.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
                return item.getUnitPrice();
            }
        }
        if (order.getSaleChannel() == SaleChannel.WHOLESALE) {
            if (variant.getPriceWholesale() != null) {
                return variant.getPriceWholesale();
            }
        }
        return resolveRetailPrice(variant, product);
    }
}
