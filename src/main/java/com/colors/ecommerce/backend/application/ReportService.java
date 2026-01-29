package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.infrastucture.adapter.IOrderCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IProductCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IProductVariantCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IShipmentCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IUserCrudRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.OrderProductEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ShipmentEntity;
import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import com.colors.ecommerce.backend.domain.port.IStockMovementRepository;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final IOrderCrudRepository orderCrudRepository;
    private final IProductVariantCrudRepository productVariantCrudRepository;
    private final IProductCrudRepository productCrudRepository;
    private final IUserCrudRepository userCrudRepository;
    private final IStockMovementRepository stockMovementRepository;
    private final IShipmentCrudRepository shipmentCrudRepository;

    public ReportService(IOrderCrudRepository orderCrudRepository,
                         IProductVariantCrudRepository productVariantCrudRepository,
                         IProductCrudRepository productCrudRepository,
                         IUserCrudRepository userCrudRepository,
                         IStockMovementRepository stockMovementRepository,
                         IShipmentCrudRepository shipmentCrudRepository) {
        this.orderCrudRepository = orderCrudRepository;
        this.productVariantCrudRepository = productVariantCrudRepository;
        this.productCrudRepository = productCrudRepository;
        this.userCrudRepository = userCrudRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.shipmentCrudRepository = shipmentCrudRepository;
    }

    public byte[] salesReport(LocalDateTime from, LocalDateTime to, OrderState state) {
        List<OrderEntity> orders = orderCrudRepository.findByOrderStateAndDateCreatedBetween(state, from, to);
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph("Lions Brand · Reporte", titleFont));
        document.add(new Paragraph("Reporte de Ventas"));
        document.add(new Paragraph("Rango: " + from.toLocalDate() + " a " + to.toLocalDate()));
        document.add(new Paragraph("Estado: " + state));
        document.add(new Paragraph(" "));

        BigDecimal totalSold = BigDecimal.ZERO;
        int totalOrders = orders.size();
        Map<String, Integer> topVariants = new HashMap<>();

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(headerCell("Orden"));
        table.addCell(headerCell("Fecha"));
        table.addCell(headerCell("Cliente"));
        table.addCell(headerCell("Total"));

        for (OrderEntity order : orders) {
            BigDecimal orderTotal = order.getTotal() == null ? BigDecimal.ZERO : order.getTotal();
            totalSold = totalSold.add(orderTotal);
            String clientName = "Sin usuario";
            if (order.getUserEntity() != null) {
                UserEntity user = userCrudRepository.findById(order.getUserEntity().getId()).orElse(null);
                if (user != null) {
                    clientName = user.getFirstName() + " " + user.getLastName();
                }
            }
            table.addCell(String.valueOf(order.getId()));
            table.addCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            table.addCell(clientName);
            table.addCell(orderTotal.toString());

            if (order.getOrderProducts() != null) {
                for (OrderProductEntity item : order.getOrderProducts()) {
                    ProductVariantEntity variant = productVariantCrudRepository.findById(item.getProductVariantId()).orElse(null);
                    if (variant != null) {
                        String key = variant.getSku();
                        topVariants.put(key, topVariants.getOrDefault(key, 0) + item.getQuantity().intValue());
                    }
                }
            }
        }

        document.add(new Paragraph("Total órdenes: " + totalOrders));
        document.add(new Paragraph("Total vendido: " + totalSold));
        document.add(new Paragraph(" "));
        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Top variantes (cantidad):"));
        for (Map.Entry<String, Integer> entry : topVariants.entrySet()) {
            document.add(new Paragraph(entry.getKey() + " - " + entry.getValue()));
        }

        document.close();
        return out.toByteArray();
    }

    public byte[] stockReport(LocalDateTime from, LocalDateTime to, Integer variantId, StockMovementType type, StockMovementReason reason) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph("Lions Brand · Reporte", titleFont));
        document.add(new Paragraph("Reporte de Stock"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell(headerCell("SKU"));
        table.addCell(headerCell("Producto"));
        table.addCell(headerCell("Talle"));
        table.addCell(headerCell("Color"));
        table.addCell(headerCell("Stock"));
        table.addCell(headerCell("Mínimo"));

        for (ProductVariantEntity variant : productVariantCrudRepository.findAll()) {
            ProductEntity product = variant.getProductEntity();
            String productName = product != null ? product.getName() : "Producto";
            String stockText = String.valueOf(variant.getStockCurrent() == null ? 0 : variant.getStockCurrent());
            String minText = String.valueOf(variant.getStockMinimum() == null ? 0 : variant.getStockMinimum());
            PdfPCell stockCell = new PdfPCell(new Phrase(stockText));
            if (variant.getStockMinimum() != null && variant.getStockCurrent() != null
                    && variant.getStockCurrent() <= variant.getStockMinimum()) {
                stockCell.setBackgroundColor(new java.awt.Color(255, 220, 220));
            }
            table.addCell(variant.getSku());
            table.addCell(productName);
            table.addCell(variant.getSize());
            table.addCell(variant.getColor());
            table.addCell(stockCell);
            table.addCell(minText);
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Movimientos de stock"));
        document.add(new Paragraph("Rango: " + from.toLocalDate() + " a " + to.toLocalDate()));

        PdfPTable movementsTable = new PdfPTable(6);
        movementsTable.setWidthPercentage(100);
        movementsTable.addCell(headerCell("Fecha"));
        movementsTable.addCell(headerCell("Variante"));
        movementsTable.addCell(headerCell("Tipo"));
        movementsTable.addCell(headerCell("Motivo"));
        movementsTable.addCell(headerCell("Cantidad"));
        movementsTable.addCell(headerCell("Usuario"));

        Iterable<StockMovement> movements = stockMovementRepository.findByFilters(variantId, from, to, type, reason);
        for (StockMovement movement : movements) {
            movementsTable.addCell(movement.getCreatedAt() != null ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
            movementsTable.addCell(movement.getVariantId() == null ? "-" : movement.getVariantId().toString());
            movementsTable.addCell(movement.getType().name());
            movementsTable.addCell(movement.getReason().name());
            movementsTable.addCell(String.valueOf(movement.getQty()));
            movementsTable.addCell(movement.getCreatedBy() == null ? "—" : movement.getCreatedBy());
        }

        document.add(movementsTable);
        document.close();
        return out.toByteArray();
    }

    public byte[] kardexReport(Integer variantId, LocalDateTime from, LocalDateTime to) {
        Iterable<StockMovement> movements = stockMovementRepository.findByVariantIdAndDateRange(variantId, from, to);
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph("Lions Brand · Reporte", titleFont));
        document.add(new Paragraph("Kardex Variante " + variantId));
        document.add(new Paragraph("Rango: " + from.toLocalDate() + " a " + to.toLocalDate()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell(headerCell("Fecha"));
        table.addCell(headerCell("Tipo"));
        table.addCell(headerCell("Motivo"));
        table.addCell(headerCell("Cantidad"));
        table.addCell(headerCell("Nota"));

        for (StockMovement movement : movements) {
            table.addCell(movement.getCreatedAt() != null ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
            table.addCell(movement.getType().name());
            table.addCell(movement.getReason().name());
            table.addCell(String.valueOf(movement.getQty()));
            table.addCell(movement.getNote() == null ? "" : movement.getNote());
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] ordersShipmentsReport(LocalDateTime from, LocalDateTime to, ShipmentStatus status, String carrier) {
        List<OrderEntity> orders = orderCrudRepository.findByDateCreatedBetween(from, to);
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph("Lions Brand · Reporte", titleFont));
        document.add(new Paragraph("Reporte de Órdenes + Envíos"));
        document.add(new Paragraph("Rango: " + from.toLocalDate() + " a " + to.toLocalDate()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.addCell(headerCell("Orden"));
        table.addCell(headerCell("Cliente"));
        table.addCell(headerCell("Fecha"));
        table.addCell(headerCell("Total"));
        table.addCell(headerCell("Estado"));
        table.addCell(headerCell("Transporte"));
        table.addCell(headerCell("Guía"));
        table.addCell(headerCell("Estado envío"));

        for (OrderEntity order : orders) {
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            if (shipment != null) {
                if (status != null && shipment.getStatus() != status) {
                    continue;
                }
                if (carrier != null && !carrier.isBlank() && shipment.getCarrier() != null
                        && !shipment.getCarrier().toLowerCase().contains(carrier.toLowerCase())) {
                    continue;
                }
            } else if (status != null || (carrier != null && !carrier.isBlank())) {
                continue;
            }

            String clientName = "Sin usuario";
            if (order.getUserEntity() != null) {
                UserEntity user = userCrudRepository.findById(order.getUserEntity().getId()).orElse(null);
                if (user != null) {
                    clientName = user.getFirstName() + " " + user.getLastName();
                }
            }
            table.addCell(String.valueOf(order.getId()));
            table.addCell(clientName);
            table.addCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            table.addCell(order.getTotal() == null ? "0" : order.getTotal().toString());
            table.addCell(order.getOrderState().name());
            table.addCell(shipment != null ? shipment.getCarrier() : "—");
            table.addCell(shipment != null ? shipment.getTrackingNumber() : "—");
            table.addCell(shipment != null && shipment.getStatus() != null ? shipment.getStatus().name() : "—");
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
        return cell;
    }
}
