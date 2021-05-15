package com.ingemark.webshop.model;

import com.ingemark.webshop.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="webshop_order")
@Getter @Setter
public class Order extends BaseEntity {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalPriceHrk;

    @Column(nullable = false)
    private BigDecimal totalPriceEur;

    @ElementCollection
    @CollectionTable(
            name = "order_item",
            joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "index_id")
    private List<OrderItem> orderItems = new ArrayList<>(0);

    public Order() {
        status = OrderStatus.DRAFT;
    }

    @Builder
    public Order(Long id, OrderStatus status, BigDecimal totalPriceHrk,
                 BigDecimal totalPriceEur, List<OrderItem> orderItems) {
        this.id = id;
        this.status = status;
        this.totalPriceHrk = totalPriceHrk;
        this.totalPriceEur = totalPriceEur;
        this.orderItems = orderItems;
    }
}
