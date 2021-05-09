package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.repository.OrderItemRepository;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Iterable<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOne(Long id) {
        return orderRepository.findById(id);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order update(Order order) {
        Order currentState = orderRepository.findById(order.getId()).orElse(null);
        if (currentState == null) {
            return null;
        }




        return orderRepository.save(currentState);
    }

    public boolean delete(Long id) {
        try {
            orderRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    @Transactional
    public boolean finalizeOrder() {

        return false;
    }

}
