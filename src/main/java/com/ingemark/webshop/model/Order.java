package com.ingemark.webshop.model;

import com.ingemark.webshop.helper.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="webshop_order")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private OrderStatus status;

    @NotNull
    private Long totalPriceHrk;

    @NotNull
    private String totalPriceEur;

}
