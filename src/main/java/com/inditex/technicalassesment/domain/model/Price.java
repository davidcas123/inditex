package com.inditex.technicalassesment.domain.model;

import com.inditex.technicalassesment.domain.exception.InvalidPriceException;
import com.inditex.technicalassesment.domain.exception.PriceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record Price(
        Long brandId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priceList,
        Long productId,
        Integer priority,
        BigDecimal price,
        Currency currency
) {

    public Price {
        validateBrandId(brandId);
        validateProductId(productId);
        validateDateRange(startDate, endDate);
        validatePriceList(priceList);
        validatePriority(priority);
        validatePriceAmount(price);
        validateCurrency(currency);
    }

    private static void validateBrandId(Long brandId) {
        if (isInvalidId(brandId)) {
            throw new InvalidPriceException("Brand ID must be greater than zero");
        }
    }

    private static void validateProductId(Long productId) {
        if (isInvalidId(productId)) {
            throw new InvalidPriceException("Product ID must be greater than zero");
        }
    }

    private static void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        requireNonNull(startDate, "Start date cannot be null");
        requireNonNull(endDate, "End date cannot be null");

        if (startDate.isAfter(endDate)) {
            throw new InvalidPriceException("Start date cannot be after end date");
        }
    }

    private static void validatePriceList(Integer priceList) {
        requireNonNull(priceList, "Price list cannot be null");

        if (priceList < 0) {
            throw new InvalidPriceException("Price list must be non-negative");
        }
    }

    private static void validatePriority(Integer priority) {
        requireNonNull(priority, "Priority cannot be null");

        if (priority < 0) {
            throw new InvalidPriceException("Priority must be non-negative");
        }
    }

    private static void validatePriceAmount(BigDecimal price) {
        requireNonNull(price, "Price amount cannot be null");

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException("Price must be non-negative");
        }
    }

    private static void validateCurrency(Currency currency) {
        requireNonNull(currency, "Currency cannot be null");
    }

    private static boolean isInvalidId(Long id) {
        return id == null || id <= 0;
    }

    private static void requireNonNull(Object object, String message) {
        if (object == null) {
            throw new InvalidPriceException(message);
        }
    }

    public boolean isApplicableAt(LocalDateTime applicationDate) {
        if (applicationDate == null) {
            return false;
        }
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }

    public static Price selectHighestPriority(List<Price> prices, LocalDateTime date) {
        return prices.stream()
                .filter(price -> price.isApplicableAt(date))
                .max(Comparator.comparing(Price::priority))
                .orElseThrow(() -> new PriceNotFoundException(
                        "No applicable price found for the given date: " + date
                ));
    }


}
