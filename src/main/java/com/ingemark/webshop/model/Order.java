package com.ingemark.webshop.model;

import com.ingemark.webshop.model.enums.OrderStatus;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@ToString
@Table(name="webshop_order")
@Getter @Setter @NoArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne
    private Customer customer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal totalPriceHrk;

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal totalPriceEur;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Set<OrderItem> orderItems;

    @PrePersist
    public void prePersist() {
        if (status == null) status = OrderStatus.DRAFT;
        if (totalPriceHrk == null) totalPriceHrk = BigDecimal.ZERO;
        if (totalPriceEur == null) totalPriceEur = BigDecimal.ZERO;
    }

    @Builder
    public Order(Long id, OrderStatus status, BigDecimal totalPriceHrk,
                 BigDecimal totalPriceEur, Set<OrderItem> orderItems) {
        this.id = id;
        this.status = status;
        this.totalPriceHrk = totalPriceHrk;
        this.totalPriceEur = totalPriceEur;
        this.orderItems = orderItems;
    }
}
