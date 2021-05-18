package com.ingemark.webshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.webshop.model.enums.OrderStatus;
import com.ingemark.webshop.exception.ArgumentNotValidException;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.model.Product;
import com.ingemark.webshop.service.CustomerService;
import com.ingemark.webshop.service.OrderService;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Order> argumentCaptor;

    @Captor
    private ArgumentCaptor<Long> argumentCaptorID;

    @MockBean
    private OrderService orderService;
    @MockBean
    private CustomerService customerService;

    private static Order testWithoutItems;
    private static Order testWithItems;

    @BeforeAll
    void prepare() {
        testWithoutItems = Order.builder().id(1L).status(OrderStatus.DRAFT).totalPriceHrk(BigDecimal.ZERO)
                .totalPriceEur(BigDecimal.ZERO).orderItems(new HashSet<>()).build();

        Product product1 = Product.builder().id(1L).code("1234567890").name("product").isAvailable(true)
                .priceHrk(BigDecimal.TEN).description("description of product").build();
        Product product2 = Product.builder().id(2L).code("0000012345").name("second product").isAvailable(true)
                .priceHrk(BigDecimal.valueOf(15.75)).description("description of a second product").build();
        OrderItem item1 = OrderItem.builder().product(product1).quantity(2).build();
        OrderItem item2 = OrderItem.builder().product(product2).quantity(3).build();

        testWithItems = Order.builder().id(2L).status(OrderStatus.DRAFT).totalPriceHrk(BigDecimal.ZERO)
                .totalPriceEur(BigDecimal.ZERO).orderItems(Sets.newHashSet(Arrays.asList(item1, item2))).build();
    }

    @Test
    @DisplayName("Should get a list of existing orders")
    public void shouldGetAllOrders() throws Exception {
        when(orderService.getAll()).thenReturn(Lists.newArrayList(
                testWithoutItems, testWithItems
        ));

        this.mockMvc
                .perform(get("/api/read-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(testWithoutItems.getStatus().toString())))
                .andExpect(jsonPath("$[0].orderItems.[*]", hasSize(testWithoutItems.getOrderItems().size())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is(testWithItems.getStatus().name())))
                .andExpect(jsonPath("$[1].orderItems.[*]", hasSize(testWithItems.getOrderItems().size())));
    }

    @Test
    @DisplayName("Should get an empty list even if orders dont exist")
    public void shouldGetAllOrders_IfEmpty() throws Exception {
        when(orderService.getAll()).thenReturn(Lists.newArrayList());

        this.mockMvc
                .perform(get("/api/read-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(0)));
    }

    @Test
    @DisplayName("Should get an existing order")
    public void shouldGetOneOrder() throws Exception {
        when(orderService.getOne(anyLong())).thenReturn(testWithoutItems);

        this.mockMvc
                .perform(get("/api/read-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.orderItems.[*]", hasSize(0)));
    }


    @Test
    @DisplayName("Should return NOT FOUND error on getting unknown id")
    public void shouldReturn404_OnGetOneOrder() throws Exception {
        when(orderService.getOne(anyLong()))
                .thenThrow(new EntityNotFoundException("Order with provided id not found"));

        this.mockMvc
                .perform(get("/api/read-orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create new order without body")
    public void shouldCreateNewOrderFromNull() throws Exception {
        Order created = Order.builder().id(3L).status(OrderStatus.DRAFT).build();
        when(orderService.save(any(Order.class))).thenReturn(created);

        this.mockMvc
                .perform(post("/api/create-order"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.orderItems.[*]", hasSize(0)));

        verify(orderService, times(1)).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus()).isEqualTo(null);
    }

    @Test
    @DisplayName("Should create new order")
    public void shouldCreateNewOrder() throws Exception {
        Order request = Order.builder().build();
        Order created = Order.builder().id(3L).status(OrderStatus.DRAFT).build();
        when(orderService.save(any(Order.class))).thenReturn(created);

        this.mockMvc
                .perform(post("/api/create-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.orderItems.[*]", hasSize(0)));

        verify(orderService, times(1)).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus()).isEqualTo(null);
    }

    @Test
    @DisplayName("Should update the order")
    public void updateOrder() throws Exception {
        when(orderService.update(any(Order.class))).thenReturn(testWithItems);

        this.mockMvc
                .perform(put("/api/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithItems)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.orderItems.[*]", hasSize(2)));

        verify(orderService, times(1)).update(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(argumentCaptor.getValue().getTotalPriceHrk()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return 400 if arguments are not valid")
    public void shouldFailIfArgumentsInvalid_OnUpdateOrder() throws Exception {
        when(orderService.update(any()))
                .thenThrow(new ArgumentNotValidException("Object has no id"));

        this.mockMvc
                .perform(put("/api/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithoutItems)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Object has no id")));

        verify(orderService, times(0)).save(forClass(Order.class).capture());
    }


    @Test
    @DisplayName("Should fail on validation when trying to update order with negative quantity")
    public void shouldFailOnValidation_OnUpdateOrder() throws Exception {
        OrderItem failItem = OrderItem.builder().product(Product.builder().build()).quantity(-2).build();
        testWithItems.getOrderItems().add(failItem);

        this.mockMvc
                .perform(put("/api/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithItems)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Validation failed")))
                .andExpect(jsonPath("$.message",
                        Matchers.containsString("must be greater than or equal to 0")));

        // assert that method was not called
        verify(orderService, times(0)).save(forClass(Order.class).capture());
    }

    @Test
    @DisplayName("Should return NOT FOUND error on updating unknown id")
    public void shouldReturn404_OnUpdateOrder() throws Exception {
        when(orderService.update(any()))
                .thenThrow(new EntityNotFoundException("Order with provided id not found"));

        this.mockMvc
                .perform(put("/api/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testWithoutItems)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Order with provided id not found")));
    }

    @Test
    public void deleteOrder() throws Exception {
        Long orderID = 2L;
        this.mockMvc
                .perform(delete("/api/delete-order/" + orderID))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).delete(argumentCaptorID.capture());
        assertThat(argumentCaptorID.getValue()).isEqualTo(orderID);
    }

    @Test
    public void deleteOrderFail_IfDoesntExist() throws Exception {
        doThrow(new EntityNotFoundException()).when(orderService).delete(anyLong());

        Long orderID = 5L;
        this.mockMvc
                .perform(delete("/api/delete-order/" + orderID))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).delete(argumentCaptorID.capture());
        assertThat(argumentCaptorID.getValue()).isEqualTo(orderID);
    }

    @Test
    public void finalizeOrderFail_IfDoesntExist() throws Exception {
        Long orderID = 5L;
        when(orderService.finalizeOrder(any()))
                .thenThrow(new EntityNotFoundException("Order with provided id not found"));

        this.mockMvc
                .perform(post("/api/finalize-order/" + orderID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Order with provided id not found")));

        verify(orderService, times(1)).finalizeOrder(argumentCaptorID.capture());
        assertThat(argumentCaptorID.getValue()).isEqualTo(orderID);
    }

    @Test
    public void finalizeOrderFail_IfNoItems() throws Exception {
        Long orderID = 5L;
        when(orderService.finalizeOrder(any()))
                .thenThrow(new ArgumentNotValidException("Order doesn't have any items"));

        this.mockMvc
                .perform(post("/api/finalize-order/" + orderID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order doesn't have any items")));

        verify(orderService, times(1)).finalizeOrder(argumentCaptorID.capture());
        assertThat(argumentCaptorID.getValue()).isEqualTo(orderID);
    }

    @Test
    public void finalizeOrderFail_AlreadyFinalized() throws Exception {
        when(orderService.finalizeOrder(any()))
                .thenThrow(new ArgumentNotValidException("Order already finalized"));

        Long orderID = 5L;
        this.mockMvc
                .perform(post("/api/finalize-order/" + orderID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order already finalized")));

        verify(orderService, times(1)).finalizeOrder(argumentCaptorID.capture());
        assertThat(argumentCaptorID.getValue()).isEqualTo(orderID);
    }


}
