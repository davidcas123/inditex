package com.inditex.technicalassesment.application.service;

import com.inditex.technicalassesment.application.dto.query.FindPriceQuery;
import com.inditex.technicalassesment.application.dto.response.PriceResponse;
import com.inditex.technicalassesment.application.port.in.FindApplicablePriceUseCase;
import com.inditex.technicalassesment.domain.model.Price;
import com.inditex.technicalassesment.domain.port.out.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindApplicablePriceService implements FindApplicablePriceUseCase {

    private final PriceRepository priceRepository;

    @Override
    public PriceResponse execute(FindPriceQuery query) {
        List<Price> prices = priceRepository.findApplicablePrices(
                query.applicationDate(),
                query.productId(),
                query.brandId()
        );

        Price applicablePrice = Price.selectHighestPriority(
                prices,
                query.applicationDate()
        );
        return PriceResponse.from(applicablePrice);
    }
}
