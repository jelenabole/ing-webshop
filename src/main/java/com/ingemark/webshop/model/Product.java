package com.ingemark.webshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product extends BaseEntity implements Serializable {

    @NotNull
    @Column(length=10, unique=true)
    @Size(min=10, max=10)
    private String code;

    @NotNull
    private String name;

    @NotNull
    private Float priceHrk;

    private String description;

    @NotNull
    private Boolean isAvailable;

}
