package com.ingemark.webshop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum HNBCurrency {
    EUR("?valuta=EUR"),
    HRK("?valuta=HRK");

    private final String url;

}
