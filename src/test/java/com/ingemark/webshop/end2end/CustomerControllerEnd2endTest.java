package com.ingemark.webshop.end2end;

import com.ingemark.webshop.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({ "test", "integration-test" })
public class CustomerControllerEnd2endTest {

    @Autowired
    private WebTestClient webClient;

    String customerUrl = "/api/customer";

    @Test
    @DisplayName("should get all customers from the database")
    public void getListOfObjects() {
        Customer[] customer = webClient.get().uri(customerUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer[].class).returnResult().getResponseBody();

        assertThat(customer).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("should get customer from the database")
    public void getOneObject() {
        Customer customer = webClient.get().uri(customerUrl + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class).returnResult().getResponseBody();

        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("John");
        assertThat(customer.getLastName()).isEqualTo("Doe");
        assertThat(customer.getEmail()).endsWith("email.com");
    }

    @Test
    @DisplayName("should create new customer")
    public void createNewObject() {
        Customer result = webClient.post().uri(customerUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Customer.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
    }
}
