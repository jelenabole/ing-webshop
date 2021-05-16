package com.ingemark.webshop.service;

import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {
    private OrderService orderService;
    private ProductService productService;
    private HNBService hnbService;
    private OrderRepository orderRepository;

    private static Order testWithoutItems;
    private static Order testWithItems;
    private static OrderItem orderItem1;

    @BeforeAll
    void prepare() {
        testWithoutItems = Order.builder().id(1L).status(OrderStatus.DRAFT).totalPriceHrk(BigDecimal.ZERO)
                .totalPriceEur(BigDecimal.ZERO).orderItems(Sets.newHashSet()).build();

        Product product1 = Product.builder().id(1L).code("1234567890").name("product").isAvailable(true)
                .priceHrk(BigDecimal.TEN).description("description of product").build();
        Product product2 = Product.builder().id(2L).code("0000012345").name("second product").isAvailable(true)
                .priceHrk(BigDecimal.valueOf(15.75)).description("description of a second product").build();
        orderItem1 = OrderItem.builder().product(product1).quantity(2).build();
        OrderItem orderItem2 = OrderItem.builder().product(product2).quantity(3).build();

        testWithItems = Order.builder().id(2L).status(OrderStatus.DRAFT).totalPriceHrk(BigDecimal.ZERO)
                .totalPriceEur(BigDecimal.ZERO).orderItems(Sets.newHashSet(Arrays.asList(orderItem1, orderItem2)))
                .build();
    }

    @BeforeEach
    void setupService() {
        orderRepository = mock(OrderRepository.class);
        productService = mock(ProductService.class);
        hnbService = mock(HNBService.class);
        orderService = new OrderService(orderRepository, productService, hnbService);
    }

    @Test
    void testGetAll() {
        when(orderRepository.findAll()).thenReturn(Lists.newArrayList(
                testWithItems, testWithoutItems
        ));

        List<Order> result = orderService.getAll();

        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.get(1).getStatus()).isEqualTo(OrderStatus.DRAFT);
    }

    @Test
    void testGetAll_ifEmtpy() {
        when(orderRepository.findAll()).thenReturn(Lists.newArrayList());
        List<Order> result = orderService.getAll();

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void testGetOne() {
        Long objectID = 1L;
        when(orderRepository.findById(objectID)).thenReturn(Optional.of(testWithoutItems));

        Order result = orderService.getOne(objectID);

        assertThat(result.getId()).isEqualTo(objectID);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.getOrderItems().size()).isEqualTo(0);
    }

    @Test
    void testGetOne_ObjectNotFound() {
        Long objectID = 3L;
        when(orderRepository.findById(objectID))
                .thenThrow(new EntityNotFoundException("Order with provided id not found"));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> orderService.getOne(objectID));

        verify(orderRepository, times(1)).findById(anyLong());
        assertThat(exception.getMessage()).startsWith("Order with provided id not found");
    }

    @Test
    void testSave() {
        Order newOrder = Order.builder().status(OrderStatus.DRAFT).build();
        when(orderRepository.save(any(Order.class))).thenReturn(testWithoutItems);

        Order result = orderService.save(newOrder);

        // objects are the same if they have same values (id is ignored)
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
    }

    @Test
    void testUpdate() {
        Long objectID = 1L;
        Order update = Order.builder().id(1L).status(OrderStatus.DRAFT).totalPriceHrk(BigDecimal.ZERO)
                .totalPriceEur(BigDecimal.ZERO).orderItems(Sets.newHashSet(Arrays.asList(orderItem1))).build();

        when(orderRepository.findById(objectID)).thenReturn(Optional.of(testWithoutItems));
        when(orderRepository.save(any(Order.class))).thenReturn(update);

        Order result = orderService.update(update);
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));

        Assertions.assertThat(result.getId()).isEqualTo(objectID);
        Assertions.assertThat(result.getOrderItems().size()).isEqualTo(1);
    }

    @Test
    void testUpdate_ObjectNotFound() {
        Long objectID = 3L;
        when(orderRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Order with provided id not found"));
        Exception exception = assertThrows(EntityNotFoundException.class, () -> orderService.update(testWithoutItems));

        verify(orderRepository, times(1)).findById(anyLong());
        assertThat(exception.getMessage()).startsWith("Order with provided id not found");
    }

    @Test
    void testDelete() {
        doNothing().when(orderRepository).deleteById(anyLong());
        orderRepository.deleteById(1L);
        verify(orderRepository, times(1)).deleteById(anyLong());
    }
}