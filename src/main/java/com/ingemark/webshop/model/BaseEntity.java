package com.ingemark.webshop.model;

import lombok.Getter;
import javax.persistence.*;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}