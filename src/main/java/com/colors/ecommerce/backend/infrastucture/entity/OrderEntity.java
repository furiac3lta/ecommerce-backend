package com.colors.ecommerce.backend.infrastucture.entity;

import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.PaymentMethod;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime dateCreated;
    @Enumerated(value= EnumType.STRING)
    private OrderState orderState;
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;
    private BigDecimal total;
    private LocalDateTime paidAt;
    @Enumerated(value = EnumType.STRING)
    private SaleChannel saleChannel;
    @Column(unique = true)
    private String orderNumber;
    @Enumerated(value = EnumType.STRING)
    private com.colors.ecommerce.backend.domain.model.DeliveryType deliveryType;
    private java.time.LocalDate estimatedDeliveryDate;
    private java.time.LocalDate actualDeliveryDate;
    private BigDecimal balanceDue;
    private BigDecimal balanceCredit;
    @ManyToOne
    private UserEntity userEntity;
    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.PERSIST)
    private List<OrderProductEntity> orderProducts;

}
