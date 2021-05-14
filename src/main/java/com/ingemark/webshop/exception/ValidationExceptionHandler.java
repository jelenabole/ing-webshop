package com.ingemark.webshop.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ValidationExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, Object> body = createExceptionBody(ex);
        body.put("status", HttpStatus.NOT_FOUND.value());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        body.put("field-errors", fieldErrors);

        return body;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityError(DataIntegrityViolationException ex) {
        Map<String, Object> body = createExceptionBody(ex);
        body.put("status", HttpStatus.CONFLICT.value());
        return body;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public Object handleObjectNotFoundException(ObjectNotFoundException ex) {
        Map<String, Object> body = createExceptionBody(ex);
        body.put("status", HttpStatus.NOT_FOUND.value());
        return body;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public Object handleObjectNotFoundException(EmptyResultDataAccessException ex) {
        Map<String, Object> body = createExceptionBody(ex);
        body.put("status", HttpStatus.NOT_FOUND.value());
        return body;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    @ExceptionHandler(WebClientResponseException.class)
    public Object handleObjectNotFoundException(WebClientResponseException ex) {
        Map<String, Object> body = createExceptionBody(ex);
        body.put("status", HttpStatus.FAILED_DEPENDENCY.value());
        body.put("additionalMessage", "HNB API error");
        return body;
    }

    private Map<String, Object> createExceptionBody(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", ex.getClass().getSimpleName());
        return body;
    }

}