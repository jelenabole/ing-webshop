package com.ingemark.webshop.model;

import lombok.Getter;
import javax.persistence.*;

@MappedSuperclass
@Getter
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}