package com.ingemark.webshop.service;

import com.ingemark.webshop.enums.HNBCurrency;
import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.exception.ObjectNotFoundException;
import com.ingemark.webshop.domain.ExchangeRateData;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.repository.OrderItemRepository;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final HNBService hnbService;

    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        orderRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    public Order getOne(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), id));
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order update(Order order) {
        if (order.getId() == null) throw new RuntimeException("Object has no id");

        Order currentState = orderRepository.findById(order.getId())
                .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), order.getId()));
        List<Long> ids = currentState.getOrderItems().stream()
                .map(OrderItem::getId).collect(Collectors.toList());
        Iterable<OrderItem> checkItems = orderItemRepository.findAllById(ids);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public Order finalizeOrder(Long orderId) {
        ExchangeRateData euroRate = hnbService.getExchangeRate(HNBCurrency.EUR);

        // do check out in DB transaction
        return checkOut(orderId, euroRate);
    }

    @Transactional
    public Order checkOut(Long orderId, ExchangeRateData euroRate) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ObjectNotFoundException(Order.class.getSimpleName(), orderId));
        if (order.getOrderItems().isEmpty())
            throw new ObjectNotFoundException(OrderItem.class.getSimpleName(), orderId);

        BigDecimal totalPriceInHrk = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            totalPriceInHrk = totalPriceInHrk.add(
                    orderItem.getProduct().getPriceHrk().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        BigDecimal totalPriceInEur = euroRate.getMiddleRate().multiply(totalPriceInHrk);

        order.setTotalPriceHrk(totalPriceInHrk.setScale(2, RoundingMode.HALF_UP));
        order.setTotalPriceEur(totalPriceInEur.setScale(2, RoundingMode.HALF_UP));
        order.setStatus(OrderStatus.SUBMITTED);

        return orderRepository.save(order);
    }
}
