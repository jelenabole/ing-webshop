package com.ingemark.webshop.hnb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ingemark.webshop.hnb.helper.HNBDecimalDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateData {

    // omitted data

    @JsonProperty("sifra_valute")
    private String currencyCode;

    @JsonProperty("datum_primjene")
    private String dateOfApplication;

    @JsonProperty("valuta")
    private String currency;

    @JsonProperty("jedinica")
    private Integer unit;

    @JsonProperty("kupovni_tecaj")
    @JsonDeserialize(using = HNBDecimalDeserializer.class)
    private BigDecimal buyingRate;

    @JsonProperty("srednji_tecaj")
    @JsonDeserialize(using = HNBDecimalDeserializer.class)
    private BigDecimal middleRate;

    @JsonProperty("prodajni_tecaj")
    @JsonDeserialize(using = HNBDecimalDeserializer.class)
    private BigDecimal sellingRate;

}
