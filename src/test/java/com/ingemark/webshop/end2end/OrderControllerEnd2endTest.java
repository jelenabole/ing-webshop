package com.ingemark.webshop.end2end;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ingemark.webshop.enums.OrderStatus;
import com.ingemark.webshop.model.Order;
import com.ingemark.webshop.model.OrderItem;
import com.ingemark.webshop.service.ProductService;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource("/application-integration-test.properties")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerEnd2endTest {

    private WireMockServer wireMockServer;
    private final BigDecimal zeroDecimal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private WebTestClient webClient;

    // this service is not tested, use only for getting test values
    @Autowired
    private ProductService productService;

    @BeforeAll
    void setUp() {
        wireMockServer = new WireMockServer(9090);
        // exchange rate of EUR set lower to notice the difference between real and mocked api
        String example = "[{\"broj_tecajnice\":\"94\",\"datum_primjene\":\"2021-05-17\",\"drzava\":\"EMU\"," +
                "\"drzava_iso\":\"EMU\",\"sifra_valute\":\"978\",\"valuta\":\"EUR\",\"jedinica\":1," +
                "\"kupovni_tecaj\":\"5,499015\",\"srednji_tecaj\":\"5,521580\",\"prodajni_tecaj\":\"5,544145\"}]";
        wireMockServer.stubFor(WireMock.get("/tecajn/v2?valuta=EUR")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(example)));
        wireMockServer.start();
    }

    @AfterAll
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("should get all orders from the database")
    public void getListOfObjects() {
        Order[] result = webClient.get().uri("/api/read-orders")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order[].class).returnResult().getResponseBody();

        assertThat(result).isNotNull().hasAtLeastOneElementOfType(Order.class).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("should get order from the database")
    public void getOneObject() {
        Order result = webClient.get().uri("/api/read-orders/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.getTotalPriceHrk()).isEqualTo(zeroDecimal);
        assertThat(result.getTotalPriceEur()).isEqualTo(zeroDecimal);
        assertThat(result.getOrderItems()).hasSize(1);
        assertThat(result.getCustomer().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("should return NotFound when getting uknown object")
    public void getOneObject_404() {
        webClient.get().uri("/api/read-orders/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order with provided id not found");
    }

    @Test
    @DisplayName("should create new order")
    public void createNewObject_FromNull() {
        Order result = webClient.post().uri("/api/create-order")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.getTotalPriceHrk()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTotalPriceEur()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getOrderItems()).isNull();
        assertThat(result.getCustomer().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("should update existing order - add only available items")
    public void updateObject_AddItems() {
        OrderItem item1 = OrderItem.builder().product(productService.getOne(1L)).quantity(2).build();
        OrderItem item2 = OrderItem.builder().product(productService.getOne(2L)).quantity(5).build();
        OrderItem item3 = OrderItem.builder().product(productService.getOne(3L)).quantity(5).build();
        Order newObject = Order.builder().id(3L)
                .orderItems(Sets.newHashSet(Arrays.asList(item1, item2, item3))).build();

        // 1 - add only available items (remove item3)
        Order result = webClient.put().uri("/api/update-order")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newObject), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTotalPriceHrk()).isEqualTo(zeroDecimal);
        assertThat(result.getTotalPriceEur()).isEqualTo(zeroDecimal);
        assertThat(result.getOrderItems()).isNotNull().hasSize(2);

        // 2 - test quantity change and removing 0 qunatity items (remove item1)
        result.getOrderItems().remove(item1);
        result.getOrderItems().remove(item2);
        item2.setQuantity(1);
        item1.setQuantity(0);
        result.getOrderItems().addAll(Lists.newArrayList(item1, item2));

        Order result2 = webClient.put().uri("/api/update-order")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newObject), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(result2).isNotNull();
        assertThat(result2.getTotalPriceHrk()).isEqualTo(zeroDecimal);
        assertThat(result2.getTotalPriceEur()).isEqualTo(zeroDecimal);
        assertThat(result2.getOrderItems()).isNotNull().hasSize(1);
        System.out.println(result2);
    }

    @Test
    @DisplayName("should return Validation Exception on updating invalid order")
    public void updateObject_400() {
        OrderItem item1 = OrderItem.builder().product(productService.getOne(1L)).quantity(-2).build();
        Order newObject = Order.builder().id(2L).orderItems(Sets.newHashSet(Collections.singletonList(item1))).build();

        webClient.put().uri("/api/update-order")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newObject), Order.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(Matchers.containsString("Validation failed"))
                .jsonPath("$.message").value(Matchers.containsString("must be greater than or equal to 0"));
    }

    @Test
    @DisplayName("should return Argument Invalid Exception on updating finalized order")
    public void updateObject_400_OrderFinalized() {
        OrderItem item1 = OrderItem.builder().product(productService.getOne(1L)).quantity(2).build();
        Order newObject = Order.builder().id(2L).orderItems(Sets.newHashSet(Collections.singletonList(item1))).build();

        webClient.put().uri("/api/update-order")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newObject), Order.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(Matchers.containsString("Object already finalized"));
    }

    @Test
    @DisplayName("should return NotFound Exception on updating existing order")
    public void updateObject_404() {
        Order newObject = Order.builder().id(10L).build();

        webClient.put().uri("/api/update-order")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newObject), Order.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.message").isEqualTo("Order with provided id not found");
    }

    @Test
    @DisplayName("should delete existing order")
    public void deleteObject() {
        webClient.delete().uri("/api/delete-order/4")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("should return NotFound when deleting non-existent order")
    public void deleteObject_404() {
        webClient.delete().uri("/api/delete-order/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(Matchers.containsString("entity with id 10 exists"));
    }

    @Test
    @DisplayName("should return Exception when finalizing order that isn't valid")
    public void finalizeObject_404() {
        // 1 - not found
        webClient.post().uri("/api/finalize-order/10")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.message").isEqualTo("Order with provided id not found");

        // 2 - already finalized
        webClient.post().uri("/api/finalize-order/2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").isEqualTo("Order already finalized");

        // 3 - no items
        webClient.post().uri("/api/finalize-order/4")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").isEqualTo("Order doesn't have any items");
    }

    @Test
    @DisplayName("should finalize the order")
    public void finalizeObject() {
        Order result = webClient.post().uri("/api/finalize-order/5")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTotalPriceHrk()).isEqualTo(BigDecimal.valueOf(64.50)
                .setScale(2, RoundingMode.HALF_UP));
        assertThat(result.getTotalPriceEur()).isEqualTo(BigDecimal.valueOf(11.68)
                .setScale(2, RoundingMode.HALF_UP));
        assertThat(result.getOrderItems()).isNotNull().hasSize(2);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
        System.out.println(result);
    }
}
