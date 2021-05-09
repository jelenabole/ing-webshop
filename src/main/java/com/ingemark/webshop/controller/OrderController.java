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
    public ResponseEntity<Order> readOrder(@PathVariable Long id) {
        return orderService.getOne(id)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        return new ResponseEntity<>(orderService.save(order), HttpStatus.CREATED);
    }

    @PutMapping({ "/update-order", "/update-order/{id}" })
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        if (order.getId() == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Order saved = orderService.update(order);

        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/delete-order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long id) {
        if (orderService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }

    @GetMapping("/finalize-order")
    public ResponseEntity<Order> finalizeOrder() {
        if (orderService.finalizeOrder()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

}
