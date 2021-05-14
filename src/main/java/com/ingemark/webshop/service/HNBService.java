package com.ingemark.webshop.service;

import com.ingemark.webshop.domain.ExchangeRateData;
import com.ingemark.webshop.enums.HNBCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HNBService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final WebClient HNBApiClient;

    ExchangeRateData getExchangeRate(HNBCurrency currency) {
        List<ExchangeRateData> exchangeRates = HNBApiClient.get()
                .uri(currency.getUrl())
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .bodyToFlux(ExchangeRateData.class)
                .collectList().block(REQUEST_TIMEOUT);

        if (exchangeRates == null || exchangeRates.isEmpty())
            throw new RuntimeException("HNB API - value empty");
        return exchangeRates.get(0);
    }

}
