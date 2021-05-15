package com.ingemark.webshop.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class OrderItem {

    @NotNull
    @OneToOne
    @JoinColumn(name="product_id")
    @EqualsAndHashCode.Include
    private Product product;

    @Min(0)
    @NotNull
    @EqualsAndHashCode.Include
    private Integer quantity;

    @Builder
    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
