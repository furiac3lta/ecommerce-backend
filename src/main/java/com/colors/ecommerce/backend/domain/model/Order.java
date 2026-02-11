package com.colors.ecommerce.backend.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private LocalDateTime dateCreated;
    private List<OrderProduct> orderProducts;
    private OrderState orderState;
    private PaymentMethod paymentMethod;
    private BigDecimal total;
    private LocalDateTime paidAt;
    private Integer userId;
    private SaleChannel saleChannel;
    private String orderNumber;
    private DeliveryType deliveryType;
    private java.time.LocalDate estimatedDeliveryDate;
    private java.time.LocalDate actualDeliveryDate;
    private BigDecimal balanceDue;
    private BigDecimal balanceCredit;

    public Order() {
        orderProducts = new ArrayList<>();
    }
    public BigDecimal getTotalOrderPrice() {
        return this.orderProducts.stream().map(orderProduct -> orderProduct.getTotalItem()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
