package com.ingemark.webshop.service;

import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    private final OrderRepository orderRepository;
    private final WebClient HNBApiClient;

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
        
        return orderRepository.save(order);
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
        ExchangeRateData currentRate = getEuroValue();

        return false;
    }

    private ExchangeRateData getEuroValue() {
        ExchangeRateData[] exchangeRateData = HNBApiClient
                .get()
                .uri("?valuta=EUR")
                .retrieve()
                .bodyToMono(ExchangeRateData[].class)
                .block(REQUEST_TIMEOUT);

        if (exchangeRateData != null && exchangeRateData.length == 1) {
            return exchangeRateData[0];
        } else {
            // error
            return null;
        }
    }
}
