package com.ingemark.webshop.form;

import com.ingemark.webshop.model.OrderItem;
import lombok.*;

import javax.validation.Valid;
import java.util.Set;

@ToString
@Getter @Setter @NoArgsConstructor
public class OrderForm {

    @Valid
    private Set<OrderItem> orderItems;

    @Builder
    public OrderForm(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
