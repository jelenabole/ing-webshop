package com.ingemark.webshop.service;

import com.ingemark.webshop.enums.HNBCurrency;
import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.handler.ObjectNotFoundException;
import com.ingemark.webshop.model.ExchangeRateData;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), orderId));
        if (order.getOrderItems().isEmpty())
            throw new ObjectNotFoundException(OrderItem.class.getSimpleName(), orderId);

        BigDecimal totalPriceInHrk = new BigDecimal(0);
        for (OrderItem orderItem : order.getOrderItems()) {
            totalPriceInHrk = totalPriceInHrk.add(
                    BigDecimal.valueOf(orderItem.getQuantity() * orderItem.getProduct().getPriceHrk()));
        }
        BigDecimal totalPriceInEur = euroRate.getMiddleRate().multiply(totalPriceInHrk);


        order.setTotalPriceHrk(totalPriceInHrk.round(new MathContext(2, RoundingMode.CEILING)).floatValue());
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
