package com.inditex.technicalassesment.domain.model;

import com.inditex.technicalassesment.domain.exception.InvalidPriceException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Currency {
    EUR("EUR");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    public static Currency fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidPriceException("Currency code cannot be null or blank");
        }

        return Arrays.stream(Currency.values())
                .filter(currency -> currency.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new InvalidPriceException(
                        "Invalid currency code: " + code
                ));
    }

    @Override
    public String toString() {
        return code;
    }
}
