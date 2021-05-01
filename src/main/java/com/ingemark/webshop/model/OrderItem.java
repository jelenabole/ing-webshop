package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderItem extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @OneToOne
    @JoinColumn(name="product_id")
    private Product product;

    @NotNull
    private int quantity;

}
