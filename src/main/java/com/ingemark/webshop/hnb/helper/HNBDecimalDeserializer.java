package com.ingemark.webshop.hnb.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class HNBDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        String string = parser.getText();
        string = string.replace(",", ".");
        return new BigDecimal(string);
    }
}
