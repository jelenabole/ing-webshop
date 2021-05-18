package com.ingemark.webshop.end2end;

import com.ingemark.webshop.model.Product;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({ "test", "integration-test" })
public class ProductControllerEnd2endTest {

    @Autowired
    private WebTestClient webClient;

    String productUrl = "/api/products";

    @Test
    @DisplayName("should get all products from the database")
    public void getListOfObjects() {
        Product[] products = webClient.get().uri(productUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product[].class).returnResult().getResponseBody();

        assertThat(products).isNotNull().hasAtLeastOneElementOfType(Product.class).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("should get product from the database")
    public void getOneObject() {
        Product result = webClient.get().uri(productUrl + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("0000000001");
        assertThat(result.getName()).isEqualTo("Product for get");
        assertThat(result.getPriceHrk()).isEqualTo(BigDecimal.valueOf(17.25));
        assertThat(result.isAvailable()).isEqualTo(true);
    }

    @Test
    @DisplayName("should return NotFound when getting uknown object")
    public void getOneObject_404() {
        webClient.get().uri(productUrl + "/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Product with provided id not found");
    }

    @Test
    @DisplayName("should create new product")
    public void createNewObject() {
        Product newProduct = Product.builder().code("0000000005").name("Product 5")
                .priceHrk(BigDecimal.TEN).isAvailable(false).build();

        Product result = webClient.post().uri(productUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newProduct), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("0000000005");
        assertThat(result.getName()).isEqualTo("Product 5");
        assertThat(result.getPriceHrk()).isEqualTo(BigDecimal.TEN);
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("should return Validation Exception on creating new product")
    public void createNewObject_400() {
        Product newProduct = Product.builder().name("Product 4").isAvailable(false).build();

        webClient.post().uri(productUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newProduct), Product.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(Matchers.containsString("Validation failed"))
                .jsonPath("$.message").value(Matchers.containsString("must not be null"));
    }

    @Test
    @DisplayName("should return Data Integrity Exception on creating new product")
    public void createNewObject_409_UniqueConstraint() {
        Product newProduct = Product.builder().code("0000000001").name("Product")
                .priceHrk(BigDecimal.TEN).isAvailable(false).build();

        webClient.post().uri(productUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newProduct), Product.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message").value(
                        Matchers.containsString("ConstraintViolationException: could not execute statement"));
    }

    @Test
    @DisplayName("should update existing product")
    public void updateObject() {
        Product newProduct = Product.builder().id(1L).code("0000000025").name("New name")
                .priceHrk(BigDecimal.ZERO).isAvailable(false).build();

        Product result = webClient.put().uri(productUrl + "/2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newProduct), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("0000000002");
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getPriceHrk()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getDescription()).isNull();
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("should return Validation Exception on updating existing product")
    public void updateObject_400() {
        Product newProduct = Product.builder().id(1L).code("0000000025").name("New name")
                .isAvailable(false).build();

        webClient.put().uri(productUrl + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newProduct), Product.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(Matchers.containsString("Validation failed"))
                .jsonPath("$.message").value(Matchers.containsString("must not be null"));
    }

    @Test
    @DisplayName("should delete existing product")
    public void deleteObject() {
        webClient.delete().uri(productUrl + "/3")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("should return NotFound when deleting non-existent product")
    public void deleteObject_404() {
        webClient.delete().uri(productUrl + "/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
