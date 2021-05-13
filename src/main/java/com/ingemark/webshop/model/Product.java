package com.ingemark.webshop.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Product extends BaseEntity {

    @NotNull
    @Column(length=10, unique=true, updatable=false)
    @Size(min=10, max=10, message="code must have exactly 10 characters")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal priceHrk;

    private String description;

    @NotNull
    private Boolean isAvailable;

}
