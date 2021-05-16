package com.ingemark.webshop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isAvailable")
    private boolean isAvailable;

    @Builder
    public Product(Long id, String code, String name, BigDecimal priceHrk,
                   String description, boolean isAvailable) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.priceHrk = priceHrk;
        this.description = description;
        this.isAvailable = isAvailable;
    }

}
