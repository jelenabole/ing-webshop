package com.ingemark.webshop.service;

import com.ingemark.webshop.domain.ExchangeRateData;
import com.ingemark.webshop.enums.HNBCurrency;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HNBService {

    private static final Logger logger = LoggerFactory.getLogger(HNBService.class);

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final WebClient HNBApiClient;

    /**
     * Fetches the exchange data for different currencies from HNB api.
     *
     * @param currency HNBCurrency enum value that represent the currency's url params
     * @return Exchange Data object with the values for a given currency
     */
    ExchangeRateData getExchangeRate(HNBCurrency currency) {
        logger.info("getExchangeRate is called - for currency: " + currency.name());
        List<ExchangeRateData> exchangeRates = HNBApiClient.get()
                .uri(currency.getUrl())
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .bodyToFlux(ExchangeRateData.class)
                .collectList().block(REQUEST_TIMEOUT);

        if (exchangeRates == null || exchangeRates.isEmpty())
            throw new WebClientResponseException(1, "HNB API error- value empty", null, null, null);

        return exchangeRates.get(0);
    }

}
