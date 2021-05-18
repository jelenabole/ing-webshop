package com.ingemark.webshop.hnb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum HNBCurrency {
    EUR("/tecajn/v2?valuta=EUR"),
    // other examples
    USD("/tecajn/v2?valuta=USD");

    private final String urlPath;

}
