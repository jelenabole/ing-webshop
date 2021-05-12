package com.ingemark.webshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String buyingRate;

    @JsonProperty("srednji_tecaj")
    private String middleRate;

    @JsonProperty("prodajni_tecaj")
    private String sellingRate;

}
