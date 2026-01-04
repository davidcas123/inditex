package com.inditex.technicalassesment.domain.port.out;

import com.inditex.technicalassesment.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepository {
    Optional<Price> getByProductAndChainOnDate(LocalDateTime applicationDate, Long productId, Long brandId);
}
