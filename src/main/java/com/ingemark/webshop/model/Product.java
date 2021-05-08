package com.ingemark.webshop.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product extends BaseEntity implements Serializable {

    @NotNull
    @Column(length=10, unique=true, updatable=false)
    @Size(min=10, max=10, message="code must have exactly 10 characters")
    @Setter(AccessLevel.NONE)
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private Float priceHrk;

    private String description;

    @NotNull
    private Boolean isAvailable;

}
