package com.ingemark.webshop.integration;

import com.ingemark.webshop.hnb.model.ExchangeRateData;
import com.ingemark.webshop.hnb.enums.HNBCurrency;
import com.ingemark.webshop.service.HNBService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-integration-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HnbServiceIntegrationTest {

    @Autowired
    private HNBService hnbService;

    @Test
    void testWireMock() {
        ExchangeRateData data = hnbService.getExchangeRate(HNBCurrency.EUR);

        // values are not zero
        assertThat(data).isNotNull();
        assertThat(data.getCurrencyCode()).isNotNull();
        assertThat(data.getDateOfApplication()).isNotNull();
        assertThat(data.getCurrency()).isNotNull();
        assertThat(data.getUnit()).isNotNull();
        assertThat(data.getBuyingRate()).isNotNull();
        assertThat(data.getMiddleRate()).isNotNull();
        assertThat(data.getSellingRate()).isNotNull();
    }

}