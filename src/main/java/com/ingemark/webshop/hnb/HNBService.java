package com.ingemark.webshop.hnb;

import com.ingemark.webshop.hnb.model.ExchangeRateData;
import com.ingemark.webshop.hnb.enums.HNBCurrency;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HNBService {

    private static final Logger logger = LoggerFactory.getLogger(HNBService.class);

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final WebClient hnbApiClient;

    /**
     * Fetches the exchange data for different currencies from HNB api.
     *
     * @param urlParams HBN api specific params, can be represented with HNBCurrency enum
     * @return Exchange Data object with the values for a given currency
     */
    public ExchangeRateData getExchangeRate(String urlParams) {
        logger.info("getExchangeRate is called - url params: {}", urlParams);
        List<ExchangeRateData> exchangeRates = hnbApiClient
                .get().uri(urlParams)
                .retrieve()
                .onStatus(HttpStatus::isError, ClientResponse::createException)
                .bodyToFlux(ExchangeRateData.class)
                .collectList().block(REQUEST_TIMEOUT);

        if (exchangeRates == null || exchangeRates.isEmpty())
            throw new WebClientResponseException(1, "HNB API error- value empty", null, null, null);

        logger.info("getExchangeRate is called - retrieved rate: {}", exchangeRates.get(0).getMiddleRate());
        return exchangeRates.get(0);
    }

    /**
     * Fetches only middle rate from exchange data for specific currencies from HNB api.
     *
     * @param forCurrency HNBCurrency name (enum) that represent the currency's url params
     * @return BigDecimal value of middle exchange rate
     */
    public BigDecimal getMiddleExchangeRate(HNBCurrency forCurrency) {
        logger.info("getMiddleExchangeRate is called - for currency: {}", forCurrency.name());
        ExchangeRateData data = getExchangeRate(forCurrency.getUrlPath());

        return data.getMiddleRate();
    }

}
