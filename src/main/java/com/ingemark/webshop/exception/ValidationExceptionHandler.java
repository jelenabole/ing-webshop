package com.ingemark.webshop.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ValidationExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException ex) {
        logger.warn("Controller method argument validation failed");
        Map<String, Object> body = createExceptionBody(ex, HttpStatus.BAD_REQUEST);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        body.put("field-errors", fieldErrors);

        logger.warn("Field errors: {}", fieldErrors);
        return body;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ HttpMessageNotReadableException.class, ArgumentNotValidException.class })
    public Map<String, Object> handleWrongArguments(RuntimeException ex) {
        logger.warn("Bad Request - Controller Method Argument Validation Exception. Message: {}", ex.getMessage());
        return createExceptionBody(ex, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityError(DataIntegrityViolationException ex) {
        logger.warn("Conflict - Data Integrity Violation Exception. Message: {}", ex.getMessage());
        return createExceptionBody(ex, HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ EntityNotFoundException.class, EmptyResultDataAccessException.class }) // get, delete
    public Object handleObjectNotFoundException(RuntimeException ex) {
        logger.warn("Not Found - Entity Not Found. Message: {}", ex.getMessage());
        return createExceptionBody(ex, HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    @ExceptionHandler(WebClientResponseException.class)
    public Object handleObjectNotFoundException(WebClientResponseException ex) {
        logger.warn("Failed Dependency - HNB Client Exception. Message: {}", ex.getMessage());
        Map<String, Object> body = createExceptionBody(ex, HttpStatus.FAILED_DEPENDENCY);
        body.put("additionalMessage", "HNB Client Exception");
        return body;
    }

    private Map<String, Object> createExceptionBody(Exception ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", ex.getClass().getSimpleName());
        body.put("status", status.value());

        return body;
    }

}