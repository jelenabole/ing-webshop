package com.ingemark.webshop.exception;

import lombok.Getter;

@Getter
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String name, Long id) {
        super(String.format("%s with id %d not found", name, id));
    }

}