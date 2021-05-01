package com.ingemark.webshop.model;

import lombok.Getter;
import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue
    private Long id;
}