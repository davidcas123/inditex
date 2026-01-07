package com.inditex.technicalassesment.application.dto.response;

import com.inditex.technicalassesment.domain.model.Price;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PriceResponse(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {
    public static PriceResponse from(Price price) {
        return PriceResponse.builder()
                .productId(price.productId())
                .brandId(price.brandId())
                .priceList(price.priceList())
                .startDate(price.startDate())
                .endDate(price.endDate())
                .price(price.price())
                .currency(price.currency().getCode())
                .build();
    }
}
