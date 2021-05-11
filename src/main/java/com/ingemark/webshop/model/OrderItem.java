package com.ingemark.webshop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
