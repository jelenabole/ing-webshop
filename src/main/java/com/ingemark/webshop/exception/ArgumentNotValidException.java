package com.ingemark.webshop.exception;

/**
 * Error thrown when arguments are not valid by business rules.
 */
public class ArgumentNotValidException extends RuntimeException {

    public ArgumentNotValidException(String name) {
        super(name);
    }

}