package com.ingemark.webshop.service;

import com.ingemark.webshop.enums.HNBCurrency;
import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.exception.ArgumentNotValidException;
import com.ingemark.webshop.domain.ExchangeRateData;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final HNBService hnbService;

    /**
     * Returns all existing orders.
     *
     * @return fetched orders as a List
     */
    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        orderRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    /**
     * Returns one order with a given ID.
     *
     * @param id ID of the order to fetch
     * @return fetched order
     * @throws RuntimeException thrown if order with provided ID does not exist
     */
    public Order getOne(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with provided id not found"));
    }

    /**
     * Persists new order.
     *
     * @param order order to save
     * @return saved order
     */
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Updates items of the order. Adds only items that are currently available and which quantity is not zero.
     *
     * @param order object to update
     * @return updated order
     * @throws RuntimeException thrown if order doesn't exist, doesn't have ID, or is already finalized
     */
    @Transactional
    public Order update(Order order) {
        if (order.getId() == null) throw new ArgumentNotValidException("Object has no id");

        Order currentState = orderRepository.findById(order.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order with provided id not found"));
        if (order.getStatus() == OrderStatus.SUBMITTED) throw new ArgumentNotValidException("Object already finalized");
        Set<OrderItem> fromRequest = order.getOrderItems();

        // 1 - remove items that are 0
        fromRequest.removeIf(item -> item.getQuantity() == 0);

        // 2 - find items that are not available
        Set<OrderItem> newItems = new HashSet<>(fromRequest) {{ removeAll(currentState.getOrderItems()); }};
        if (newItems.size() != 0) {
            List<Long> ids = newItems.stream().map(item -> item.getProduct().getId()).collect(Collectors.toList());
            List<Product> notAvailable = productService.getAllByIds(ids);
            notAvailable.removeIf(Product::isAvailable);

            // 3 - remove items that are not available
            fromRequest.removeIf(item -> notAvailable.contains(item.getProduct()));
        }

        // 4 - remove items that are not in filtered request, and add new ones / update quantites
        currentState.getOrderItems().removeIf(item -> !fromRequest.contains(item));
        currentState.getOrderItems().addAll(fromRequest);

        // omitted calc for current price
        return orderRepository.save(currentState);
    }

    /**
     * Deletes one order by id.
     *
     * @param id ID of the order to delete
     * @throws RuntimeException thrown if order doesn't exist
     */
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    /**
     *  Checks validity of the order, then calculates the total price for all currencies,
     *  and changes order status to SUBMITTED.
     *
     * @param orderId ID of the order to finalize
     * @return finalized order or error
     * @throws RuntimeException thrown if order doesn't exist, is already finalized or doesn't contain any items
     */
    public Order finalizeOrder(Long orderId) {
        // validate order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with provided id not found"));
        if (order.getStatus().equals(OrderStatus.SUBMITTED))
            throw new ArgumentNotValidException("Order already finalized");
        if (order.getOrderItems().isEmpty())
            throw new ArgumentNotValidException("Order doesn't have any items");

        ExchangeRateData euroRate = hnbService.getExchangeRate(HNBCurrency.EUR);

        // calculate prices
        calculateTotalPrice(order);
        BigDecimal totalPriceInEur = euroRate.getMiddleRate().multiply(order.getTotalPriceHrk());
        order.setTotalPriceEur(totalPriceInEur.setScale(2, RoundingMode.HALF_UP));

        order.setStatus(OrderStatus.SUBMITTED);
        return orderRepository.save(order);
    }

    /**
     * Calculates total price of all existing items in the order.
     *
     * @param order order with the items
     */
    private void calculateTotalPrice(Order order) {
        BigDecimal totalPrice = order.getOrderItems().stream()
                .map(item -> item.getProduct().getPriceHrk().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPriceHrk(totalPrice);
        order.setTotalPriceHrk(totalPrice.setScale(2, RoundingMode.HALF_UP));
    }

}
