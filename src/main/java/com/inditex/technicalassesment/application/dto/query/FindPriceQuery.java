package com.inditex.technicalassesment.application.dto.query;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record FindPriceQuery(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {
    public FindPriceQuery {
        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date cannot be null");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        if (brandId == null || brandId <= 0) {
            throw new IllegalArgumentException("Brand ID must be positive");
        }
    }
}
