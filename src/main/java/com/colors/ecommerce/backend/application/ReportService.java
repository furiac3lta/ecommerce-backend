package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.StockMovement;
import com.colors.ecommerce.backend.domain.model.StockMovementReason;
import com.colors.ecommerce.backend.domain.model.StockMovementType;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.domain.model.DeliveryType;
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
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import com.colors.ecommerce.backend.domain.model.ShipmentStatus;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPageEventHelper;
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
    private final IStockReservationRepository stockReservationRepository;

    public ReportService(IOrderCrudRepository orderCrudRepository,
                         IProductVariantCrudRepository productVariantCrudRepository,
                         IProductCrudRepository productCrudRepository,
                         IUserCrudRepository userCrudRepository,
                         IStockMovementRepository stockMovementRepository,
                         IShipmentCrudRepository shipmentCrudRepository,
                         IStockReservationRepository stockReservationRepository) {
        this.orderCrudRepository = orderCrudRepository;
        this.productVariantCrudRepository = productVariantCrudRepository;
        this.productCrudRepository = productCrudRepository;
        this.userCrudRepository = userCrudRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.shipmentCrudRepository = shipmentCrudRepository;
        this.stockReservationRepository = stockReservationRepository;
    }

    public byte[] manualUsuarioReport() {
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new ManualFooter("Manual de Usuario"));
        document.open();

        addManualHeader(document, "Manual de Usuario - LION'S BRAND");

        addSectionTitle(document, "1. Introduccion");
        addParagraph(document, "Este manual explica como comprar y hacer seguimiento de tu pedido en la tienda online de LION'S BRAND.");
        addParagraph(document, "El pago es por transferencia (PaymentMethod.TRANSFERENCIA) y la confirmacion se realiza manualmente.");
        addParagraph(document, "Para comprar necesitas iniciar sesion o crear cuenta. En el checkout podes registrarte en el momento.");
        addParagraph(document, "Al confirmar la compra, el sistema reserva stock por 30 minutos mientras realizas el pago.");
        addCallout(document, "Importante", "Si la reserva vence, la orden puede cancelarse automaticamente.", new java.awt.Color(255, 247, 224));
        addPlaceholder(document, "Captura: Home / Productos");

        addSectionTitle(document, "2. Flujo de compra paso a paso");
        addParagraph(document, "1) Buscar producto en Inicio o Productos.");
        addParagraph(document, "2) Abrir el detalle, elegir talle/color y agregar al carrito.");
        addParagraph(document, "3) Revisar el carrito y ajustar cantidades.");
        addParagraph(document, "4) En el checkout iniciar sesion o crear cuenta.");
        addParagraph(document, "5) Confirmar compra: la orden queda en estado PENDING.");
        addParagraph(document, "6) Realizar la transferencia y enviar comprobante por WhatsApp.");
        addParagraph(document, "7) El admin confirma el pago y la orden pasa a COMPLETED.");
        addParagraph(document, "8) Se carga el envio: CREATED -> SHIPPED -> DELIVERED.");
        addPlaceholder(document, "Diagrama: Compra -> Pago -> Confirmacion -> Envio -> Entrega");

        addSectionTitle(document, "3. Carrito y checkout");
        addParagraph(document, "En el carrito vas a ver producto, talle, color, cantidad y subtotal.");
        addParagraph(document, "Si un item tiene entrega diferida, se muestra un aviso con fecha o dias estimados.");
        addParagraph(document, "En el checkout podes:");
        addParagraph(document, "- Iniciar sesion con tu email y contrasena.");
        addParagraph(document, "- Crear cuenta en el momento (tu telefono se usa como contrasena en este flujo).");
        addParagraph(document, "Al confirmar la compra veras el aviso: 'Reservamos tu stock por 30 minutos mientras realizas el pago.'.");
        addPlaceholder(document, "Captura: Carrito / Checkout (login y registro)");

        addSectionTitle(document, "4. Pago por transferencia y WhatsApp");
        addParagraph(document, "Luego de crear la orden se muestran los datos de transferencia: alias, CBU, banco, titular, orden y monto.");
        addParagraph(document, "Desde el boton 'Enviar comprobante por WhatsApp' se genera un mensaje con los datos del pedido.");
        addParagraph(document, "Ejemplo de mensaje:");
        addCodeBlock(document, String.join("\n",
                "Hola, realice una compra en Lions Brand.",
                "",
                "Orden: #123",
                "Fecha: 07/02/2026 13:52",
                "Nombre: Juan Perez",
                "Email: juan@email.com",
                "Telefono: 11 1234-5678",
                "Direccion: Calle 123",
                "Monto total: $25000",
                "",
                "Detalle:",
                "- Producto A2 Negro x1 ($25000) - Var#45",
                "",
                "Adjunto comprobante de transferencia."));
        addCallout(document, "Tip", "Verifica que el numero de WhatsApp sea el correcto antes de enviar.", new java.awt.Color(232, 248, 255));
        addPlaceholder(document, "Captura: Datos de transferencia + boton WhatsApp");

        addSectionTitle(document, "5. Estados de la orden");
        PdfPTable orderStates = new PdfPTable(2);
        orderStates.setWidthPercentage(100);
        orderStates.setSpacingBefore(6);
        orderStates.setSpacingAfter(8);
        orderStates.addCell(headerCellDark("Estado"));
        orderStates.addCell(headerCellDark("Que significa"));
        addZebraRow(orderStates, false, "PENDING", "Pedido creado, falta confirmar el pago.");
        addZebraRow(orderStates, true, "COMPLETED", "Pago confirmado, el pedido entra en preparacion.");
        addZebraRow(orderStates, false, "CANCELLED", "Pedido cancelado (reserva vencida o cancelacion admin).");
        document.add(orderStates);

        addSectionTitle(document, "6. Entregas con demora");
        addParagraph(document, "Algunos productos tienen delivery_type = DELAYED.");
        addParagraph(document, "En el carrito y en tu orden se muestra fecha o dias estimados.");
        addCallout(document, "Importante", "La fecha estimada se guarda en la orden y puede diferir de cambios posteriores del producto.", new java.awt.Color(255, 247, 224));

        addSectionTitle(document, "7. Mis ordenes y seguimiento");
        addParagraph(document, "Desde tu cuenta (/profile) podes ver tus pedidos, el estado y el tracking.");
        addParagraph(document, "Cuando el envio esta cargado vas a ver empresa, medio, numero de guia y estado.");
        PdfPTable shipmentStates = new PdfPTable(2);
        shipmentStates.setWidthPercentage(100);
        shipmentStates.setSpacingBefore(6);
        shipmentStates.setSpacingAfter(8);
        shipmentStates.addCell(headerCellDark("Estado envio"));
        shipmentStates.addCell(headerCellDark("Que significa"));
        addZebraRow(shipmentStates, false, "CREATED", "Envio cargado, pendiente de despacho.");
        addZebraRow(shipmentStates, true, "SHIPPED", "Pedido despachado.");
        addZebraRow(shipmentStates, false, "DELIVERED", "Pedido entregado.");
        document.add(shipmentStates);
        addParagraph(document, "Tambien podes abrir el timeline para ver eventos como pago confirmado, stock descontado y entrega.");
        addPlaceholder(document, "Captura: Mis ordenes / Detalle de envio / Timeline");

        addSectionTitle(document, "8. Devoluciones y cambios (vision usuario)");
        addParagraph(document, "Las devoluciones o cambios se gestionan por WhatsApp con el admin.");
        addParagraph(document, "Informacion a enviar:");
        addParagraph(document, "- Numero de orden.");
        addParagraph(document, "- Producto/variante y cantidad.");
        addParagraph(document, "- Motivo y nota.");
        addCallout(document, "Tip", "Si es un cambio, indica el nuevo talle/color.", new java.awt.Color(232, 248, 255));
        addParagraph(document, "Cuando se registra, el movimiento aparece en tu orden (RETURN / EXCHANGE).");

        addSectionTitle(document, "9. Preguntas frecuentes");
        addParagraph(document, "Cuanto tarda? Depende de si el producto es inmediato o con demora. La fecha estimada se ve en la orden.");
        addParagraph(document, "Como envio el comprobante? Usa el boton de WhatsApp en el checkout.");
        addParagraph(document, "Que pasa si se vencio la reserva? La orden puede cancelarse automaticamente.");
        addParagraph(document, "Como se mi numero de guia? Se ve en el detalle de la orden cuando se carga el envio.");
        addCallout(document, "Errores comunes", "No enviar el comprobante o enviar el numero de orden incorrecto.", new java.awt.Color(255, 238, 238));

        document.close();
        return out.toByteArray();
    }

    public byte[] manualAdminReport() {
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new ManualFooter("Manual Admin"));
        document.open();

        addManualHeader(document, "Manual Admin - Operacion y Backoffice");

        addSectionTitle(document, "1. Acceso y roles");
        addParagraph(document, "Las rutas /api/v1/admin/** requieren ROLE_ADMIN.");
        addParagraph(document, "El login se realiza en /api/v1/security/login y devuelve JWT con UserType (ADMIN/USER).");
        addCallout(document, "Importante", "Trabaja siempre con un usuario ADMIN para evitar errores 401/403.", new java.awt.Color(255, 247, 224));
        addPlaceholder(document, "Captura: Login Admin");

        addSectionTitle(document, "2. Mapa de modulos (Admin)");
        PdfPTable routes = new PdfPTable(new float[]{2f, 4f});
        routes.setWidthPercentage(100);
        routes.setSpacingBefore(6);
        routes.setSpacingAfter(8);
        routes.addCell(headerCellDark("Ruta"));
        routes.addCell(headerCellDark("Uso"));
        addZebraRow(routes, false, "/admin/product", "Listado y edicion de productos");
        addZebraRow(routes, true, "/admin/product/addproduct", "Alta de productos y variantes");
        addZebraRow(routes, false, "/admin/category", "Categorias");
        addZebraRow(routes, true, "/admin/orders", "Ordenes, pagos, reportes");
        addZebraRow(routes, false, "/admin/stock", "Stock y movimientos (kardex)");
        addZebraRow(routes, true, "/admin/deliveries", "Entregas y reportes de envios");
        addZebraRow(routes, false, "/admin/orders-kanban", "Kanban de entregas");
        document.add(routes);
        addPlaceholder(document, "Captura: Menu Admin");

        addSectionTitle(document, "3. Productos y variantes");
        addParagraph(document, "Pantallas: /admin/product y /admin/product/addproduct.");
        addParagraph(document, "Producto: nombre, descripcion, precio, categoria, sellOnline, deliveryType, fecha o dias estimados, nota, activo.");
        addParagraph(document, "Variante: talle, color, SKU, stock actual, stock minimo, precio retail/wholesale.");
        addCallout(document, "Tip", "Si sellOnline = false, el producto no se muestra en la web pero si en Admin.", new java.awt.Color(232, 248, 255));
        addPlaceholder(document, "Captura: ABM de productos y variantes");

        addSectionTitle(document, "4. Ordenes (operacion diaria)");
        addParagraph(document, "Pantalla: /admin/orders.");
        addParagraph(document, "Estados: PENDING, COMPLETED, CANCELLED.");
        addParagraph(document, "Confirmar pago (COMPLETED) descuenta stock y registra kardex.");
        addParagraph(document, "Cancelar orden libera reservas y marca CANCELLED.");
        addParagraph(document, "Canales: ONLINE / WHOLESALE / OFFLINE.");
        addParagraph(document, "Numeracion: WEB-000001, MAY-000001, OFF-000001.");
        addCallout(document, "Importante", "No se puede cargar envio en ordenes PENDING.", new java.awt.Color(255, 247, 224));
        addPlaceholder(document, "Captura: Ordenes + acciones + reportes");

        addSectionTitle(document, "5. Stock y Kardex");
        addParagraph(document, "Pantalla: /admin/stock.");
        addParagraph(document, "Movimientos: IN, OUT, ADJUST.");
        addParagraph(document, "Motivos frecuentes: RESTOCK, MANUAL_ADJUST, RETURN, EXCHANGE_IN, EXCHANGE_OUT, SALE_ONLINE, SALE_WHOLESALE, SALE_OFFLINE.");
        addParagraph(document, "Reglas: no stock negativo y nota obligatoria en cada movimiento.");
        addCallout(document, "Importante", "Cada ajuste queda auditado (quien y cuando).", new java.awt.Color(255, 247, 224));
        addPlaceholder(document, "Captura: Stock + Movimientos");

        addSectionTitle(document, "6. Devoluciones y cambios");
        addParagraph(document, "Desde ordenes COMPLETED podes registrar devoluciones o cambios.");
        addParagraph(document, "Devolucion: genera movimiento IN (RETURN) y ajuste de venta.");
        addParagraph(document, "Cambio: genera IN (EXCHANGE_IN) + OUT (EXCHANGE_OUT) y ajusta saldo.");
        addCallout(document, "Errores comunes", "No ingresar nota o cantidad valida: la operacion se bloquea.", new java.awt.Color(255, 238, 238));
        addPlaceholder(document, "Captura: Devoluciones / Cambios");

        addSectionTitle(document, "7. Envios y tracking");
        addParagraph(document, "Pantallas: /admin/orders y /admin/deliveries.");
        addParagraph(document, "Campos: carrier, trackingNumber, shippingMethod, destinatario, direccion, notas, status.");
        addParagraph(document, "Estados: CREATED, SHIPPED, DELIVERED.");
        addParagraph(document, "Si el envio esta en DELIVERED no se puede editar.");
        addCallout(document, "Tip", "El cliente ve estos datos en su cuenta.", new java.awt.Color(232, 248, 255));
        addPlaceholder(document, "Captura: Cargar / editar envio");

        addSectionTitle(document, "8. Kanban de entregas");
        addParagraph(document, "Pantalla: /admin/orders-kanban.");
        addParagraph(document, "Columnas: Listas para entregar, Con demora, Bloqueadas por stock, Pendientes de pago.");
        addParagraph(document, "Prioridad: COMPLETED + IMMEDIATE primero.");
        addPlaceholder(document, "Captura: Kanban de entregas");

        addSectionTitle(document, "9. Reportes PDF");
        addParagraph(document, "Reportes: Ventas, Stock, Kardex, Entregas, Ordenes + Envios, Entregadas, Kanban.");
        addParagraph(document, "Filtros: fechas, canal, estado, envio, motivo.");
        addParagraph(document, "Manual Admin: /api/v1/admin/reports/manual-admin.pdf");
        addParagraph(document, "Manual Usuario: /api/v1/admin/reports/manual-usuario.pdf");
        addCallout(document, "Tip", "Usa rangos de 30 dias para evitar PDFs pesados.", new java.awt.Color(232, 248, 255));
        addPlaceholder(document, "Captura: Reportes");

        addSectionTitle(document, "10. Troubleshooting");
        addParagraph(document, "- Orden COMPLETED sin stock descontado: revisar reservas activas y movimientos.");
        addParagraph(document, "- Stock negativo: no deberia ocurrir, revisar ajustes manuales.");
        addParagraph(document, "- Tracking no visible al usuario: revisar estado de envio y orderId.");
        addParagraph(document, "- 401/403 en Admin: token vencido o rol incorrecto.");

        document.close();
        return out.toByteArray();
    }
    public byte[] salesReport(LocalDateTime from, LocalDateTime to, OrderState state, SaleChannel saleChannel) {
        List<OrderEntity> orders = saleChannel == null
                ? orderCrudRepository.findByOrderStateAndDateCreatedBetween(state, from, to)
                : orderCrudRepository.findByOrderStateAndSaleChannelAndDateCreatedBetween(state, saleChannel, from, to);
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        addReportHeader(document, "Reporte de Ventas", from, to);
        document.add(new Paragraph("Estado: " + state, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        if (saleChannel != null) {
            document.add(new Paragraph("Canal: " + saleChannel, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        }
        document.add(new Paragraph(" "));

        BigDecimal totalSold = BigDecimal.ZERO;
        int totalOrders = orders.size();
        Map<String, Integer> topVariants = new HashMap<>();

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell(headerCellDark("Orden"));
        table.addCell(headerCellDark("Fecha"));
        table.addCell(headerCellDark("Canal"));
        table.addCell(headerCellDark("Cliente"));
        table.addCell(headerCellDark("Total"));

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
            table.addCell(bodyCell(order.getOrderNumber() == null ? String.valueOf(order.getId()) : order.getOrderNumber(), java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getSaleChannel() == null ? "ONLINE" : order.getSaleChannel().name(), java.awt.Color.WHITE));
            table.addCell(bodyCell(clientName, java.awt.Color.WHITE));
            table.addCell(bodyCell(orderTotal.toString(), java.awt.Color.WHITE));

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

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(10);
        summary.addCell(summaryCell("Total órdenes", String.valueOf(totalOrders)));
        summary.addCell(summaryCell("Total vendido", totalSold.toString()));
        summary.addCell(summaryCell("Canal", saleChannel == null ? "Todos" : saleChannel.name()));
        summary.addCell(summaryCell("Estado", state.name()));
        document.add(summary);
        document.add(new Paragraph(" "));
        document.add(table);

        java.math.BigDecimal adjustmentsTotal = java.math.BigDecimal.ZERO;
        PdfPTable adjustmentsTable = new PdfPTable(6);
        adjustmentsTable.setWidthPercentage(100);
        adjustmentsTable.addCell(headerCellDark("Orden"));
        adjustmentsTable.addCell(headerCellDark("Fecha"));
        adjustmentsTable.addCell(headerCellDark("Motivo"));
        adjustmentsTable.addCell(headerCellDark("Variante"));
        adjustmentsTable.addCell(headerCellDark("Cantidad"));
        adjustmentsTable.addCell(headerCellDark("Monto"));

        Iterable<StockMovement> adjustments = stockMovementRepository.findByDateRange(from, to);
        for (StockMovement movement : adjustments) {
            if (movement.getOrderId() == null) {
                continue;
            }
            if (movement.getReason() != StockMovementReason.RETURN
                    && movement.getReason() != StockMovementReason.EXCHANGE_IN
                    && movement.getReason() != StockMovementReason.EXCHANGE_OUT) {
                continue;
            }
            java.math.BigDecimal amount = movement.getUnitPrice() == null
                    ? java.math.BigDecimal.ZERO
                    : movement.getUnitPrice().multiply(java.math.BigDecimal.valueOf(movement.getQty() == null ? 0 : movement.getQty()));
            if (movement.getReason() == StockMovementReason.RETURN || movement.getReason() == StockMovementReason.EXCHANGE_IN) {
                amount = amount.negate();
            }
            adjustmentsTotal = adjustmentsTotal.add(amount);

            String orderLabel = String.valueOf(movement.getOrderId());
            OrderEntity order = orderCrudRepository.findById(movement.getOrderId()).orElse(null);
            if (order != null && order.getOrderNumber() != null) {
                orderLabel = order.getOrderNumber();
            }
            adjustmentsTable.addCell(bodyCell(orderLabel, java.awt.Color.WHITE));
            adjustmentsTable.addCell(bodyCell(movement.getCreatedAt() != null ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-", java.awt.Color.WHITE));
            adjustmentsTable.addCell(bodyCell(movement.getReason().name(), java.awt.Color.WHITE));
            adjustmentsTable.addCell(bodyCell(movement.getVariantId() == null ? "-" : movement.getVariantId().toString(), java.awt.Color.WHITE));
            adjustmentsTable.addCell(bodyCell(String.valueOf(movement.getQty() == null ? 0 : movement.getQty()), java.awt.Color.WHITE));
            adjustmentsTable.addCell(bodyCell(amount.toString(), java.awt.Color.WHITE));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Ajustes por devoluciones/cambios: " + adjustmentsTotal));
        document.add(new Paragraph(" "));
        document.add(adjustmentsTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Top variantes (cantidad):"));
        for (Map.Entry<String, Integer> entry : topVariants.entrySet()) {
            document.add(new Paragraph(entry.getKey() + " - " + entry.getValue()));
        }

        document.close();
        return out.toByteArray();
    }

    public byte[] stockReport(LocalDateTime from, LocalDateTime to, Integer variantId, StockMovementType type, StockMovementReason reason) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        addReportHeader(document, "Reporte de Stock", from, to);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell(headerCellDark("SKU"));
        table.addCell(headerCellDark("Producto"));
        table.addCell(headerCellDark("Talle"));
        table.addCell(headerCellDark("Color"));
        table.addCell(headerCellDark("Stock"));
        table.addCell(headerCellDark("Mínimo"));

        int lowStock = 0;
        for (ProductVariantEntity variant : productVariantCrudRepository.findAll()) {
            ProductEntity product = variant.getProductEntity();
            String productName = product != null ? product.getName() : "Producto";
            String stockText = String.valueOf(variant.getStockCurrent() == null ? 0 : variant.getStockCurrent());
            String minText = String.valueOf(variant.getStockMinimum() == null ? 0 : variant.getStockMinimum());
            PdfPCell stockCell = bodyCell(stockText, java.awt.Color.WHITE);
            if (variant.getStockMinimum() != null && variant.getStockCurrent() != null
                    && variant.getStockCurrent() <= variant.getStockMinimum()) {
                stockCell.setBackgroundColor(new java.awt.Color(255, 220, 220));
                lowStock++;
            }
            table.addCell(bodyCell(variant.getSku(), java.awt.Color.WHITE));
            table.addCell(bodyCell(productName, java.awt.Color.WHITE));
            table.addCell(bodyCell(variant.getSize(), java.awt.Color.WHITE));
            table.addCell(bodyCell(variant.getColor(), java.awt.Color.WHITE));
            table.addCell(stockCell);
            table.addCell(bodyCell(minText, java.awt.Color.WHITE));
        }

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(10);
        summary.addCell(summaryCell("Variantes", String.valueOf(productVariantCrudRepository.count())));
        summary.addCell(summaryCell("Stock bajo", String.valueOf(lowStock)));
        summary.addCell(summaryCell("Tipo", type == null ? "Todos" : type.name()));
        summary.addCell(summaryCell("Motivo", reason == null ? "Todos" : reason.name()));
        document.add(summary);
        document.add(new Paragraph(" "));
        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Movimientos de stock"));
        document.add(new Paragraph(" "));

        PdfPTable movementsTable = new PdfPTable(8);
        movementsTable.setWidthPercentage(100);
        movementsTable.addCell(headerCellDark("Fecha"));
        movementsTable.addCell(headerCellDark("Variante"));
        movementsTable.addCell(headerCellDark("Tipo"));
        movementsTable.addCell(headerCellDark("Motivo"));
        movementsTable.addCell(headerCellDark("Canal"));
        movementsTable.addCell(headerCellDark("Orden"));
        movementsTable.addCell(headerCellDark("Cantidad"));
        movementsTable.addCell(headerCellDark("Usuario"));

        Iterable<StockMovement> movements = stockMovementRepository.findByFilters(variantId, from, to, type, reason);
        for (StockMovement movement : movements) {
            movementsTable.addCell(bodyCell(movement.getCreatedAt() != null ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-", java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getVariantId() == null ? "-" : movement.getVariantId().toString(), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getType().name(), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getReason().name(), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getSaleChannel() == null ? "—" : movement.getSaleChannel().name(), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getOrderId() == null ? "—" : movement.getOrderId().toString(), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(String.valueOf(movement.getQty()), java.awt.Color.WHITE));
            movementsTable.addCell(bodyCell(movement.getCreatedBy() == null ? "—" : movement.getCreatedBy(), java.awt.Color.WHITE));
        }

        document.add(movementsTable);
        document.close();
        return out.toByteArray();
    }

    public byte[] kardexReport(Integer variantId, LocalDateTime from, LocalDateTime to) {
        Iterable<StockMovement> movements = stockMovementRepository.findByVariantIdAndDateRange(variantId, from, to);
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        addReportHeader(document, "Kardex Variante " + variantId, from, to);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.addCell(headerCellDark("Fecha"));
        table.addCell(headerCellDark("Tipo"));
        table.addCell(headerCellDark("Motivo"));
        table.addCell(headerCellDark("Canal"));
        table.addCell(headerCellDark("Orden"));
        table.addCell(headerCellDark("Cantidad"));
        table.addCell(headerCellDark("Nota"));

        for (StockMovement movement : movements) {
            table.addCell(bodyCell(movement.getCreatedAt() != null ? movement.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-", java.awt.Color.WHITE));
            table.addCell(bodyCell(movement.getType().name(), java.awt.Color.WHITE));
            table.addCell(bodyCell(movement.getReason().name(), java.awt.Color.WHITE));
            table.addCell(bodyCell(movement.getSaleChannel() == null ? "—" : movement.getSaleChannel().name(), java.awt.Color.WHITE));
            table.addCell(bodyCell(movement.getOrderId() == null ? "—" : movement.getOrderId().toString(), java.awt.Color.WHITE));
            table.addCell(bodyCell(String.valueOf(movement.getQty()), java.awt.Color.WHITE));
            table.addCell(bodyCell(movement.getNote() == null ? "" : movement.getNote(), java.awt.Color.WHITE));
        }

        document.add(new Paragraph(" "));
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] ordersShipmentsReport(LocalDateTime from, LocalDateTime to, ShipmentStatus status, String carrier) {
        List<OrderEntity> orders = orderCrudRepository.findByDateCreatedBetween(from, to);
        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        addReportHeader(document, "Reporte de Órdenes + Envíos", from, to);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.addCell(headerCellDark("Orden"));
        table.addCell(headerCellDark("Cliente"));
        table.addCell(headerCellDark("Fecha"));
        table.addCell(headerCellDark("Total"));
        table.addCell(headerCellDark("Estado"));
        table.addCell(headerCellDark("Transporte"));
        table.addCell(headerCellDark("Guía"));
        table.addCell(headerCellDark("Estado envío"));

        int withShipment = 0;
        int delivered = 0;
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
                withShipment++;
                if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
                    delivered++;
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
            table.addCell(bodyCell(order.getOrderNumber() == null ? String.valueOf(order.getId()) : order.getOrderNumber(), java.awt.Color.WHITE));
            table.addCell(bodyCell(clientName, java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getTotal() == null ? "0" : order.getTotal().toString(), java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getOrderState().name(), java.awt.Color.WHITE));
            table.addCell(bodyCell(shipment != null ? shipment.getCarrier() : "—", java.awt.Color.WHITE));
            table.addCell(bodyCell(shipment != null ? shipment.getTrackingNumber() : "—", java.awt.Color.WHITE));
            table.addCell(bodyCell(shipment != null && shipment.getStatus() != null ? shipment.getStatus().name() : "—", java.awt.Color.WHITE));
        }

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(10);
        summary.addCell(summaryCell("Total órdenes", String.valueOf(orders.size())));
        summary.addCell(summaryCell("Con envío", String.valueOf(withShipment)));
        summary.addCell(summaryCell("Entregadas", String.valueOf(delivered)));
        summary.addCell(summaryCell("Filtro estado", status == null ? "Todos" : status.name()));
        document.add(summary);
        document.add(new Paragraph(" "));
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] deliveredOrdersReport(LocalDateTime from, LocalDateTime to, SaleChannel saleChannel) {
        List<OrderEntity> orders = orderCrudRepository.findByDateCreatedBetween(from, to);
        java.util.List<OrderEntity> delivered = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        java.util.List<DeliveredRow> rows = new java.util.ArrayList<>();
        for (OrderEntity order : orders) {
            if (saleChannel != null && order.getSaleChannel() != saleChannel) {
                continue;
            }
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            if (shipment == null || shipment.getStatus() != ShipmentStatus.DELIVERED) {
                continue;
            }
            delivered.add(order);
            java.time.LocalDate estimated = order.getEstimatedDeliveryDate();
            java.time.LocalDate actual = order.getActualDeliveryDate();
            if (actual == null && shipment.getUpdatedAt() != null) {
                actual = shipment.getUpdatedAt().toLocalDate();
            }
            long diffDays = 0;
            boolean onTime = false;
            String statusLabel = "Sin estimar";
            if (estimated != null && actual != null) {
                diffDays = java.time.Duration.between(estimated.atStartOfDay(), actual.atStartOfDay()).toDays();
                onTime = diffDays <= 0;
                statusLabel = onTime ? "A tiempo" : "Demorada";
            }

            UserEntity user = order.getUserEntity() != null
                    ? userCrudRepository.findById(order.getUserEntity().getId()).orElse(null)
                    : null;
            String clientName = user == null ? "Sin usuario" : (user.getFirstName() + " " + user.getLastName());
            String carrier = shipment.getCarrier() == null ? "—" : shipment.getCarrier();
            String tracking = shipment.getTrackingNumber() == null ? "—" : shipment.getTrackingNumber();

            rows.add(new DeliveredRow(order, clientName, estimated, actual, diffDays, onTime, statusLabel, carrier, tracking));
        }

        long totalDelivered = rows.size();
        long withEstimate = rows.stream().filter(r -> r.estimated != null && r.actual != null).count();
        long onTimeCount = rows.stream().filter(r -> r.onTime).count();
        double onTimePct = withEstimate == 0 ? 0 : (onTimeCount * 100.0 / withEstimate);
        double avgActualDays = rows.stream()
                .filter(r -> r.actual != null && r.order.getDateCreated() != null)
                .mapToLong(r -> java.time.Duration.between(r.order.getDateCreated().toLocalDate().atStartOfDay(), r.actual.atStartOfDay()).toDays())
                .average().orElse(0);
        double avgDiff = rows.stream()
                .filter(r -> r.estimated != null && r.actual != null)
                .mapToLong(r -> r.diffDays)
                .average().orElse(0);

        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        document.add(new Paragraph("LION'S BRAND", brandFont));
        document.add(new Paragraph("Reporte de Órdenes Entregadas", titleFont));
        document.add(new Paragraph("Período: " + from.toLocalDate() + " a " + to.toLocalDate(), subtitleFont));
        document.add(new Paragraph("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subtitleFont));
        document.add(new Paragraph(" "));

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(10);
        summary.addCell(summaryCell("Total entregadas", String.valueOf(totalDelivered)));
        summary.addCell(summaryCell("% a tiempo", String.format("%.1f%%", onTimePct)));
        summary.addCell(summaryCell("Tiempo real promedio", String.format("%.1f días", avgActualDays)));
        summary.addCell(summaryCell("Diferencia promedio", String.format("%.1f días", avgDiff)));
        document.add(summary);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.2f, 1.6f, 1.6f, 1.6f, 1.1f, 1.2f, 1.6f, 1.6f});
        table.addCell(headerCellDark("Orden"));
        table.addCell(headerCellDark("Cliente"));
        table.addCell(headerCellDark("Compra"));
        table.addCell(headerCellDark("Estimada"));
        table.addCell(headerCellDark("Entregada"));
        table.addCell(headerCellDark("Dif."));
        table.addCell(headerCellDark("Estado"));
        table.addCell(headerCellDark("Medio"));
        table.addCell(headerCellDark("Guía"));

        for (DeliveredRow row : rows) {
            java.awt.Color bg = java.awt.Color.WHITE;
            table.addCell(bodyCell(row.order.getOrderNumber() == null ? String.valueOf(row.order.getId()) : row.order.getOrderNumber(), bg));
            table.addCell(bodyCell(row.clientName, bg));
            table.addCell(bodyCell(row.order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), bg));
            table.addCell(bodyCell(row.estimated == null ? "—" : row.estimated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), bg));
            table.addCell(bodyCell(row.actual == null ? "—" : row.actual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), bg));
            table.addCell(bodyCell(row.estimated == null || row.actual == null ? "—" : String.valueOf(row.diffDays), bg));
            PdfPCell statusCell = bodyCell(row.statusLabel, bg);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(statusCell);
            table.addCell(bodyCell(row.carrier, bg));
            table.addCell(bodyCell(row.tracking, bg));
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

    private PdfPCell headerCellDark(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, java.awt.Color.WHITE)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new java.awt.Color(10, 10, 10));
        cell.setPadding(7);
        return cell;
    }

    private PdfPCell bodyCell(String text, java.awt.Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "—" : text, FontFactory.getFont(FontFactory.HELVETICA, 8)));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        return cell;
    }

    private void addZebraRow(PdfPTable table, boolean zebra, String... values) {
        java.awt.Color bg = zebra ? new java.awt.Color(245, 245, 245) : java.awt.Color.WHITE;
        for (String value : values) {
            table.addCell(bodyCell(value, bg));
        }
    }

    private PdfPCell summaryCell(String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(new java.awt.Color(220, 220, 220));
        cell.setPadding(8);
        Paragraph labelP = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA, 8));
        Paragraph valueP = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        labelP.setSpacingAfter(2);
        cell.addElement(labelP);
        cell.addElement(valueP);
        return cell;
    }

    private void addManualHeader(Document document, String title) {
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        document.add(new Paragraph("LION'S BRAND", brandFont));
        document.add(new Paragraph(title, titleFont));
        document.add(new Paragraph("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subtitleFont));
        document.add(new Paragraph(" "));
    }

    private void addSectionTitle(Document document, String text) {
        Paragraph title = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        title.setSpacingBefore(10);
        title.setSpacingAfter(4);
        document.add(title);
    }

    private void addParagraph(Document document, String text) {
        Paragraph p = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, 9));
        p.setSpacingAfter(2);
        document.add(p);
    }

    private void addCodeBlock(Document document, String text) {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.COURIER, 8)));
        cell.setPadding(8);
        cell.setBackgroundColor(new java.awt.Color(245, 245, 245));
        box.addCell(cell);
        box.setSpacingAfter(6);
        document.add(box);
    }

    private void addCallout(Document document, String title, String text, java.awt.Color bg) {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(new java.awt.Color(220, 220, 220));
        Paragraph t = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        Paragraph b = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, 9));
        cell.addElement(t);
        cell.addElement(b);
        box.addCell(cell);
        box.setSpacingAfter(6);
        document.add(box);
    }

    private void addPlaceholder(Document document, String label) {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)));
        cell.setPadding(10);
        cell.setFixedHeight(60);
        cell.setBackgroundColor(new java.awt.Color(245, 245, 245));
        cell.setBorderColor(new java.awt.Color(220, 220, 220));
        box.addCell(cell);
        box.setSpacingAfter(6);
        document.add(box);
    }

    private void addReportHeader(Document document, String title, LocalDateTime from, LocalDateTime to) {
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        document.add(new Paragraph("LION'S BRAND", brandFont));
        document.add(new Paragraph(title, titleFont));
        if (from != null && to != null) {
            document.add(new Paragraph("Período: " + from.toLocalDate() + " a " + to.toLocalDate(), subtitleFont));
        }
        document.add(new Paragraph("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), subtitleFont));
    }

    private static class ManualFooter extends PdfPageEventHelper {
        private final String label;

        private ManualFooter(String label) {
            this.label = label;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(2);
            try {
                footer.setWidths(new float[]{3f, 1f});
                footer.setTotalWidth(document.right() - document.left());
                footer.getDefaultCell().setBorder(0);
                footer.addCell(new Phrase("LION'S BRAND – " + label, FontFactory.getFont(FontFactory.HELVETICA, 8)));
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                footer.addCell(new Phrase("Página " + writer.getPageNumber(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
                footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 10, writer.getDirectContent());
            } catch (Exception ignored) {
                // no-op
            }
        }
    }


    private static class DeliveredRow {
        private final OrderEntity order;
        private final String clientName;
        private final java.time.LocalDate estimated;
        private final java.time.LocalDate actual;
        private final long diffDays;
        private final boolean onTime;
        private final String statusLabel;
        private final String carrier;
        private final String tracking;

        private DeliveredRow(OrderEntity order,
                             String clientName,
                             java.time.LocalDate estimated,
                             java.time.LocalDate actual,
                             long diffDays,
                             boolean onTime,
                             String statusLabel,
                             String carrier,
                             String tracking) {
            this.order = order;
            this.clientName = clientName;
            this.estimated = estimated;
            this.actual = actual;
            this.diffDays = diffDays;
            this.onTime = onTime;
            this.statusLabel = statusLabel;
            this.carrier = carrier;
            this.tracking = tracking;
        }
    }

    private static class ReportFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(2);
            try {
                footer.setWidths(new float[]{3f, 1f});
                footer.setTotalWidth(document.right() - document.left());
                footer.getDefaultCell().setBorder(0);
                footer.addCell(new Phrase("LION'S BRAND – Reporte de entregas", FontFactory.getFont(FontFactory.HELVETICA, 8)));
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                footer.addCell(new Phrase("Página " + writer.getPageNumber(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
                footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 10, writer.getDirectContent());
            } catch (Exception ignored) {
                // no-op
            }
        }
    }

    public byte[] deliveriesReport(LocalDateTime from,
                                   LocalDateTime to,
                                   DeliveryType deliveryType,
                                   ShipmentStatus status,
                                   SaleChannel saleChannel) {
        List<OrderEntity> orders = orderCrudRepository.findByDateCreatedBetween(from, to);
        java.time.LocalDate today = java.time.LocalDate.now();

        java.util.List<OrderEntity> filtered = new java.util.ArrayList<>();
        for (OrderEntity order : orders) {
            if (saleChannel != null && order.getSaleChannel() != saleChannel) {
                continue;
            }
            DeliveryType orderDelivery = order.getDeliveryType() == null ? DeliveryType.IMMEDIATE : order.getDeliveryType();
            if (deliveryType != null && orderDelivery != deliveryType) {
                continue;
            }
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            if (status != null && (shipment == null || shipment.getStatus() != status)) {
                continue;
            }
            filtered.add(order);
        }

        filtered.sort((a, b) -> {
            boolean overdueA = a.getEstimatedDeliveryDate() != null
                    && a.getEstimatedDeliveryDate().isBefore(today)
                    && a.getActualDeliveryDate() == null;
            boolean overdueB = b.getEstimatedDeliveryDate() != null
                    && b.getEstimatedDeliveryDate().isBefore(today)
                    && b.getActualDeliveryDate() == null;
            if (overdueA != overdueB) {
                return overdueA ? -1 : 1;
            }
            int stateA = a.getOrderState() == OrderState.COMPLETED ? 0 : 1;
            int stateB = b.getOrderState() == OrderState.COMPLETED ? 0 : 1;
            if (stateA != stateB) return Integer.compare(stateA, stateB);
            DeliveryType typeA = a.getDeliveryType() == null ? DeliveryType.IMMEDIATE : a.getDeliveryType();
            DeliveryType typeB = b.getDeliveryType() == null ? DeliveryType.IMMEDIATE : b.getDeliveryType();
            if (typeA != typeB) {
                return typeA == DeliveryType.IMMEDIATE ? -1 : 1;
            }
            java.time.LocalDate dateA = a.getEstimatedDeliveryDate();
            java.time.LocalDate dateB = b.getEstimatedDeliveryDate();
            if (dateA == null && dateB != null) return 1;
            if (dateA != null && dateB == null) return -1;
            if (dateA != null && dateB != null) {
                int cmp = dateA.compareTo(dateB);
                if (cmp != 0) return cmp;
            }
            return a.getDateCreated().compareTo(b.getDateCreated());
        });

        long totalOrders = filtered.size();
        long immediateCount = filtered.stream().filter(o -> (o.getDeliveryType() == null ? DeliveryType.IMMEDIATE : o.getDeliveryType()) == DeliveryType.IMMEDIATE).count();
        long delayedCount = totalOrders - immediateCount;
        long pendingCount = filtered.stream().filter(o -> o.getOrderState() == OrderState.PENDING).count();

        long onTimeCount = 0;
        long withEstimate = 0;
        double avgActualDays = 0;
        double avgDiff = 0;
        java.util.List<Long> actualDaysList = new java.util.ArrayList<>();
        java.util.List<Long> diffList = new java.util.ArrayList<>();

        for (OrderEntity order : filtered) {
            java.time.LocalDate estimated = order.getEstimatedDeliveryDate();
            java.time.LocalDate actual = order.getActualDeliveryDate();
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            if (actual == null && shipment != null && shipment.getUpdatedAt() != null) {
                actual = shipment.getUpdatedAt().toLocalDate();
            }
            if (estimated != null && actual != null) {
                withEstimate++;
                long diff = java.time.Duration.between(estimated.atStartOfDay(), actual.atStartOfDay()).toDays();
                diffList.add(diff);
                if (diff <= 0) {
                    onTimeCount++;
                }
            }
            if (actual != null) {
                long actualDays = java.time.Duration.between(order.getDateCreated().toLocalDate().atStartOfDay(), actual.atStartOfDay()).toDays();
                actualDaysList.add(actualDays);
            }
        }

        if (!actualDaysList.isEmpty()) {
            avgActualDays = actualDaysList.stream().mapToLong(Long::longValue).average().orElse(0);
        }
        if (!diffList.isEmpty()) {
            avgDiff = diffList.stream().mapToLong(Long::longValue).average().orElse(0);
        }

        double onTimePct = withEstimate == 0 ? 0 : (onTimeCount * 100.0 / withEstimate);

        Document document = new Document(PageSize.A4, 36, 36, 36, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        addReportHeader(document, "Reporte de Entregas", from, to);
        document.add(new Paragraph(" "));

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(10);
        summary.addCell(summaryCell("Total órdenes", String.valueOf(totalOrders)));
        summary.addCell(summaryCell("% a tiempo", String.format("%.1f%%", onTimePct)));
        summary.addCell(summaryCell("Tiempo real promedio", String.format("%.1f días", avgActualDays)));
        summary.addCell(summaryCell("Diferencia promedio", String.format("%.1f días", avgDiff)));
        document.add(summary);

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.2f, 1.6f, 1.6f, 1.6f, 1.1f, 1.2f, 1.6f, 1.6f});
        table.addCell(headerCellDark("Orden"));
        table.addCell(headerCellDark("Cliente"));
        table.addCell(headerCellDark("Compra"));
        table.addCell(headerCellDark("Estimada"));
        table.addCell(headerCellDark("Entregada"));
        table.addCell(headerCellDark("Dif."));
        table.addCell(headerCellDark("Estado"));
        table.addCell(headerCellDark("Medio"));
        table.addCell(headerCellDark("Guía"));

        for (OrderEntity order : filtered) {
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            UserEntity user = order.getUserEntity() != null
                    ? userCrudRepository.findById(order.getUserEntity().getId()).orElse(null)
                    : null;
            String clientName = user == null ? "Sin usuario" : (user.getFirstName() + " " + user.getLastName());
            java.time.LocalDate estimated = order.getEstimatedDeliveryDate();
            java.time.LocalDate actual = order.getActualDeliveryDate();
            if (actual == null && shipment != null && shipment.getUpdatedAt() != null) {
                actual = shipment.getUpdatedAt().toLocalDate();
            }
            long diff = 0;
            String statusLabel = "Sin estimar";
            if (estimated != null && actual != null) {
                diff = java.time.Duration.between(estimated.atStartOfDay(), actual.atStartOfDay()).toDays();
                statusLabel = diff <= 0 ? "A tiempo" : "Demorada";
            }
            String carrier = shipment == null ? "—" : (shipment.getCarrier() == null ? "—" : shipment.getCarrier());
            String tracking = shipment == null ? "—" : (shipment.getTrackingNumber() == null ? "—" : shipment.getTrackingNumber());

            table.addCell(bodyCell(order.getOrderNumber() == null ? String.valueOf(order.getId()) : order.getOrderNumber(), java.awt.Color.WHITE));
            table.addCell(bodyCell(clientName, java.awt.Color.WHITE));
            table.addCell(bodyCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), java.awt.Color.WHITE));
            table.addCell(bodyCell(estimated == null ? "—" : estimated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), java.awt.Color.WHITE));
            table.addCell(bodyCell(actual == null ? "—" : actual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), java.awt.Color.WHITE));
            table.addCell(bodyCell(estimated == null || actual == null ? "—" : String.valueOf(diff), java.awt.Color.WHITE));
            PdfPCell statusCell = bodyCell(statusLabel, java.awt.Color.WHITE);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(statusCell);
            table.addCell(bodyCell(carrier, java.awt.Color.WHITE));
            table.addCell(bodyCell(tracking, java.awt.Color.WHITE));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private String resolvePriorityLabel(OrderEntity order, DeliveryType deliveryType, java.time.LocalDate today) {
        if (order.getOrderState() != OrderState.COMPLETED) {
            return "EN ESPERA";
        }
        if (deliveryType == DeliveryType.IMMEDIATE) {
            return "URGENTE";
        }
        if (order.getEstimatedDeliveryDate() == null) {
            return "EN ESPERA";
        }
        if (!order.getEstimatedDeliveryDate().isAfter(today)) {
            return "URGENTE";
        }
        if (!order.getEstimatedDeliveryDate().isAfter(today.plusDays(3))) {
            return "PROXIMO";
        }
        return "EN ESPERA";
    }

    public byte[] kanbanReport() {
        List<OrderEntity> orders = new java.util.ArrayList<>();
        orderCrudRepository.findAll().forEach(orders::add);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        java.util.List<OrderEntity> ready = new java.util.ArrayList<>();
        java.util.List<OrderEntity> delayed = new java.util.ArrayList<>();
        java.util.List<OrderEntity> blocked = new java.util.ArrayList<>();
        java.util.List<OrderEntity> pending = new java.util.ArrayList<>();

        for (OrderEntity order : orders) {
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            boolean delivered = shipment != null && shipment.getStatus() == ShipmentStatus.DELIVERED;
            if (order.getOrderState() == OrderState.PENDING) {
                pending.add(order);
                continue;
            }
            if (order.getOrderState() != OrderState.COMPLETED || delivered) {
                continue;
            }
            boolean delayedOrder = isDelayed(order);
            boolean stockOk = hasStock(order, now);
            if (!stockOk) {
                blocked.add(order);
                continue;
            }
            if (delayedOrder) {
                delayed.add(order);
            } else {
                ready.add(order);
            }
        }

        ready.sort(java.util.Comparator.comparing(OrderEntity::getDateCreated));
        blocked.sort(java.util.Comparator.comparing(OrderEntity::getDateCreated));
        pending.sort(java.util.Comparator.comparing(OrderEntity::getDateCreated));
        delayed.sort((a, b) -> {
            java.time.LocalDate aDate = resolveEstimated(a);
            java.time.LocalDate bDate = resolveEstimated(b);
            if (aDate == null && bDate != null) return 1;
            if (aDate != null && bDate == null) return -1;
            if (aDate != null && bDate != null) {
                int cmp = aDate.compareTo(bDate);
                if (cmp != 0) return cmp;
            }
            return a.getDateCreated().compareTo(b.getDateCreated());
        });

        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        document.add(new Paragraph("Lions Brand · Reporte", titleFont));
        document.add(new Paragraph("Kanban de Entrega"));
        document.add(new Paragraph("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph(" "));

        PdfPTable board = new PdfPTable(4);
        board.setWidthPercentage(100);
        board.setWidths(new float[]{1f, 1f, 1f, 1f});

        board.addCell(buildKanbanColumnCell("Listas para entregar", ready));
        board.addCell(buildKanbanColumnCell("Con demora", delayed));
        board.addCell(buildKanbanColumnCell("Bloqueadas por stock", blocked));
        board.addCell(buildKanbanColumnCell("Pendientes de pago", pending));

        document.add(board);
        document.close();
        return out.toByteArray();
    }

    private boolean isDelayed(OrderEntity order) {
        if (order.getDeliveryType() == DeliveryType.DELAYED) {
            return true;
        }
        if (order.getOrderProducts() == null) {
            return false;
        }
        for (OrderProductEntity item : order.getOrderProducts()) {
            if (item.getDeliveryType() == DeliveryType.DELAYED) {
                return true;
            }
        }
        return false;
    }

    private java.time.LocalDate resolveEstimated(OrderEntity order) {
        if (order.getEstimatedDeliveryDate() != null) {
            return order.getEstimatedDeliveryDate();
        }
        java.time.LocalDate earliest = null;
        if (order.getOrderProducts() == null) {
            return null;
        }
        for (OrderProductEntity item : order.getOrderProducts()) {
            if (item.getEstimatedDeliveryDate() == null) {
                continue;
            }
            if (earliest == null || item.getEstimatedDeliveryDate().isBefore(earliest)) {
                earliest = item.getEstimatedDeliveryDate();
            }
        }
        return earliest;
    }

    private boolean hasStock(OrderEntity order, java.time.LocalDateTime now) {
        if (order.getOrderProducts() == null) {
            return true;
        }
        Map<Integer, Integer> qtyByVariant = new HashMap<>();
        for (OrderProductEntity item : order.getOrderProducts()) {
            if (item.getProductVariantId() == null) {
                continue;
            }
            qtyByVariant.merge(item.getProductVariantId(), item.getQuantity().intValue(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> entry : qtyByVariant.entrySet()) {
            ProductVariantEntity variant = productVariantCrudRepository.findById(entry.getKey()).orElse(null);
            if (variant == null) {
                continue;
            }
            int current = variant.getStockCurrent() == null ? 0 : variant.getStockCurrent();
            int reserved = stockReservationRepository.sumActiveReservedQty(variant.getId(), now);
            int available = Math.max(0, current - reserved);
            if (available < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private PdfPTable buildKanbanTable(List<OrderEntity> orders) {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.addCell(headerCell("Orden"));
        table.addCell(headerCell("Cliente"));
        table.addCell(headerCell("Fecha"));
        table.addCell(headerCell("Canal"));
        table.addCell(headerCell("Entrega"));
        table.addCell(headerCell("Fecha estimada"));
        table.addCell(headerCell("Envío"));

        for (OrderEntity order : orders) {
            ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
            UserEntity user = order.getUserEntity() != null
                    ? userCrudRepository.findById(order.getUserEntity().getId()).orElse(null)
                    : null;
            String clientName = user == null ? "Sin usuario" : (user.getFirstName() + " " + user.getLastName());
            String delivery = isDelayed(order) ? "Demorada" : "Inmediata";
            java.time.LocalDate estimated = resolveEstimated(order);

            table.addCell(order.getOrderNumber() == null ? String.valueOf(order.getId()) : order.getOrderNumber());
            table.addCell(clientName);
            table.addCell(order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            table.addCell(order.getSaleChannel() == null ? "ONLINE" : order.getSaleChannel().name());
            table.addCell(delivery);
            table.addCell(estimated == null ? "—" : estimated.toString());
            table.addCell(shipment == null || shipment.getStatus() == null ? "Pendiente" : shipment.getStatus().name());
        }

        return table;
    }

    private Paragraph sectionTitle(String title) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        return new Paragraph(title, font);
    }

    private PdfPCell buildKanbanColumnCell(String title, List<OrderEntity> orders) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        PdfPTable column = new PdfPTable(1);
        column.setWidthPercentage(100);

        PdfPCell header = new PdfPCell(new Phrase(title + " (" + orders.size() + ")", headerFont));
        header.setBorderColor(new java.awt.Color(220, 220, 220));
        header.setBackgroundColor(new java.awt.Color(245, 245, 245));
        header.setPadding(8);
        column.addCell(header);

        if (orders.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin órdenes", metaFont));
            empty.setBorderColor(new java.awt.Color(230, 230, 230));
            empty.setPadding(8);
            column.addCell(empty);
        } else {
            for (OrderEntity order : orders) {
                ShipmentEntity shipment = shipmentCrudRepository.findByOrderEntityId(order.getId()).orElse(null);
                UserEntity user = order.getUserEntity() != null
                        ? userCrudRepository.findById(order.getUserEntity().getId()).orElse(null)
                        : null;
                String clientName = user == null ? "Sin usuario" : (user.getFirstName() + " " + user.getLastName());
                String orderLabel = order.getOrderNumber() == null ? ("#" + order.getId()) : order.getOrderNumber();
                String channel = order.getSaleChannel() == null ? "ONLINE" : order.getSaleChannel().name();
                String delivery = isDelayed(order) ? "Demorada" : "Inmediata";
                java.time.LocalDate estimated = resolveEstimated(order);
                String shipmentStatus = shipment == null || shipment.getStatus() == null ? "Pendiente" : shipment.getStatus().name();

                PdfPTable card = new PdfPTable(1);
                card.setWidthPercentage(100);

                PdfPCell titleCell = new PdfPCell(new Phrase(orderLabel + " · " + clientName, bodyFont));
                titleCell.setBorder(0);
                titleCell.setPadding(4);
                card.addCell(titleCell);

                PdfPCell metaCell = new PdfPCell(new Phrase(
                        order.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                                " · " + channel, metaFont));
                metaCell.setBorder(0);
                metaCell.setPadding(4);
                card.addCell(metaCell);

                String extra = "Entrega: " + delivery;
                if (estimated != null) {
                    extra += " · " + estimated;
                }
                extra += " · Envío: " + shipmentStatus;
                PdfPCell extraCell = new PdfPCell(new Phrase(extra, metaFont));
                extraCell.setBorder(0);
                extraCell.setPadding(4);
                card.addCell(extraCell);

                PdfPCell cardCell = new PdfPCell(card);
                cardCell.setPadding(6);
                cardCell.setBorderColor(new java.awt.Color(220, 220, 220));
                column.addCell(cardCell);
            }
        }

        PdfPCell wrapper = new PdfPCell(column);
        wrapper.setBorder(0);
        wrapper.setPadding(6);
        return wrapper;
    }
}
