package com.ingemark.webshop.controller;

import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/read-orders")
    public Iterable<Order> getAllOrders() {
        return orderService.getAll();
    }

    @GetMapping("/read-orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOne(id));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        return new ResponseEntity<>(orderService.save(order), HttpStatus.CREATED);
    }

    @PutMapping({ "/update-order", "/update-order/{id}" })
    public ResponseEntity<Order> updateOrder(@PathVariable(required = false) Long id, @Valid @RequestBody Order order) {
        if (order.getId() == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(orderService.update(order.getId(), order));
    }

    @DeleteMapping("/delete-order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/finalize-order/{id}")
    public ResponseEntity<Order> finalizeOrder(@PathVariable Long id) {
        try {
            Order order = orderService.finalizeOrder(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
