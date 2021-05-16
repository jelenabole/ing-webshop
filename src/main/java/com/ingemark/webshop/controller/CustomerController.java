package com.ingemark.webshop.controller;

import com.ingemark.webshop.model.Customer;
import com.ingemark.webshop.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Dummy controller for creating test-customer.
 */
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    @GetMapping
    public Iterable<Customer> getAllCustomers() {
        logger.info("getAllCustomers is called");
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        logger.info("getCustomer is called - with id: {}", id);
        return customerService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer() {
        logger.info("createCustomer is called");
        return customerService.create();
    }

}
