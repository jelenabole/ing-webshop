package com.ingemark.webshop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@ToString
@Getter @Setter @NoArgsConstructor
public class Customer extends BaseEntity {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email(regexp=".*@.*\\..*", message = "Email should be valid")
    private String email;

}
