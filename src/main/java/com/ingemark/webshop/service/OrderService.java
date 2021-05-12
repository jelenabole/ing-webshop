package com.ingemark.webshop.service;

import com.ingemark.webshop.enums.HNBCurrency;
import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.handler.ObjectNotFoundException;
import com.ingemark.webshop.model.ExchangeRateData;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
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

    public Order getOne(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), id));
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order update(Long id, Order order) {
        Order currentState = orderRepository.findById(order.getId())
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), id));
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public Order finalizeOrder(Long orderId) {
        ExchangeRateData euroRate = getExchangeRate(HNBCurrency.EUR);

        // do check out in DB transaction
        return checkOut(orderId, euroRate);
    }

    @Transactional
    public Order checkOut(Long orderId, ExchangeRateData euroRate) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        if (order.getOrderItems().isEmpty()) return null;
        BigDecimal totalPrice = new BigDecimal(0);
        order.getOrderItems().forEach(
                el -> totalPrice.add(BigDecimal.valueOf(el.getQuantity() * el.getProduct().getPriceHrk())));
        BigDecimal totalPriceInEur = euroRate.getMiddleRate().multiply(totalPrice);
        order.setTotalPriceEur(totalPriceInEur.round(new MathContext(2, RoundingMode.CEILING)).floatValue());
        order.setStatus(OrderStatus.SUBMITTED);
        return orderRepository.save(order);
    }

    private ExchangeRateData getExchangeRate(HNBCurrency currency) {
        List<ExchangeRateData> exchangeRates = HNBApiClient.get()
                .uri(currency.getUrl())
                .retrieve()
                .bodyToFlux(ExchangeRateData.class)
                .collectList().block(REQUEST_TIMEOUT);

        if (exchangeRates.isEmpty()) return null;

        return exchangeRates.get(0);
    }
}
