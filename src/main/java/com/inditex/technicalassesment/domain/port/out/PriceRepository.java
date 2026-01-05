package com.inditex.technicalassesment.domain.port.out;

import com.inditex.technicalassesment.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository {
    List<Price> findApplicablePrices(LocalDateTime applicationDate, Long productId, Long brandId);
}
