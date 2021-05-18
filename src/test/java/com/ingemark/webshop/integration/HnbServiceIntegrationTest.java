package com.ingemark.webshop.integration;

import com.ingemark.webshop.hnb.model.ExchangeRateData;
import com.ingemark.webshop.hnb.enums.HNBCurrency;
import com.ingemark.webshop.hnb.HNBService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HnbServiceIntegrationTest {

    @Autowired
    private HNBService hnbService;

    @Test
    void testWithEUR() {
        ExchangeRateData data = hnbService.getExchangeRate(HNBCurrency.EUR.getUrlPath());

        assertThat(data).isNotNull();
        assertThat(data.getCurrencyCode()).isNotNull().isEqualTo("978");
        assertThat(data.getDateOfApplication()).isNotNull();
        assertThat(data.getCurrency()).isNotNull().isEqualTo("EUR");
        assertThat(data.getUnit()).isNotNull().isEqualTo(1);
        assertThat(data.getBuyingRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(7));
        assertThat(data.getMiddleRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(7));
        assertThat(data.getSellingRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(7));
    }

    @Test
    void testWithUSD() {
        ExchangeRateData data = hnbService.getExchangeRate(HNBCurrency.USD.getUrlPath());

        assertThat(data).isNotNull();
        assertThat(data.getCurrencyCode()).isNotNull().isEqualTo("840");
        assertThat(data.getDateOfApplication()).isNotNull();
        assertThat(data.getCurrency()).isNotNull().isEqualTo("USD");
        assertThat(data.getUnit()).isNotNull().isEqualTo(1);
        assertThat(data.getBuyingRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(5));
        assertThat(data.getMiddleRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(5));
        assertThat(data.getSellingRate()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.valueOf(5));
    }
}