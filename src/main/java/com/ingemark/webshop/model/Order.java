package com.ingemark.webshop.model;

import com.ingemark.webshop.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="webshop_order")
@Getter @Setter
public class Order extends BaseEntity {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private float totalPriceHrk;

    @Column(nullable = false)
    private float totalPriceEur;

    @OneToMany(cascade = {CascadeType.ALL}) // (mappedBy = "webshop_order")
    @JoinColumn(name = "order_id")
    private Set<OrderItem> orderItems;

    public Order() {
        status = OrderStatus.DRAFT;
    }

}
