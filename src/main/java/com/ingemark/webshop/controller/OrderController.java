package com.ingemark.webshop.controller;

import com.ingemark.webshop.model.Customer;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.service.CustomerService;
import com.ingemark.webshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/read-orders")
    public List<Order> getAllOrders() {
        logger.info("getAllOrders is called");
        return orderService.getAll();
    }

    @GetMapping("/read-orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        logger.info("getOrder is called - with id: {}", id);
        return orderService.getOne(id);
    }

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody(required = false) Order order) {
        logger.info("createOrder is called - with args: {}", (order != null ? order.toString() : null));

        // omit getting a customer
        Customer customer = customerService.getTestCustomer();
        if (order == null) order = new Order();

        order.setCustomer(customer);
        return orderService.save(order);
    }

    @PutMapping("/update-order")
    public Order updateOrder(@Valid @RequestBody Order order) {
        logger.info("updateOrder is called - with args: {}", order.toString());
        return orderService.update(order);
    }

    @DeleteMapping("/delete-order/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        logger.info("deleteOrder is called - with id: {}", id);
        orderService.delete(id);
    }

    @PostMapping("/finalize-order/{id}")
    public Order finalizeOrder(@PathVariable Long id) {
        logger.info("finalizeOrder is called - with id: {}", id);
        return orderService.finalizeOrder(id);
    }
}
