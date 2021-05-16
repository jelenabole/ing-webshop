package com.ingemark.webshop.controller;

import com.ingemark.webshop.model.Customer;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.service.CustomerService;
import com.ingemark.webshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/read-orders")
    public List<Order> getAllOrders() {
        return orderService.getAll();
    }

    @GetMapping("/read-orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOne(id);
    }

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody(required = false) Order order) {
        // omit getting user
        Customer customer = customerService.getTestUser();
        if (order == null) order = new Order();

        order.setCustomer(customer);
        return orderService.save(order);
    }

    @PutMapping("/update-order")
    public Order updateOrder(@Valid @RequestBody Order order) {
        return orderService.update(order);
    }

    @DeleteMapping("/delete-order/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }

    @PostMapping("/finalize-order/{id}")
    public Order finalizeOrder(@PathVariable Long id) {
        return orderService.finalizeOrder(id);
    }
}
