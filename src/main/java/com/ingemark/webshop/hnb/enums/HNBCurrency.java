package com.ingemark.webshop.hnb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum HNBCurrency {
    EUR("/tecajn/v2?valuta=EUR"),
    HRK("/tecajn/v2?valuta=HRK");

    private final String url;

}
