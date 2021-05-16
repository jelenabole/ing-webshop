package com.ingemark.webshop.exception;

public class ArgumentNotValidException extends RuntimeException {

    public ArgumentNotValidException(String name) {
        super(name);
    }

}