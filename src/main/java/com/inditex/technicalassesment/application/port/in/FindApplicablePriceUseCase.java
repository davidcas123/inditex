package com.inditex.technicalassesment.application.port.in;

import com.inditex.technicalassesment.application.dto.query.FindPriceQuery;
import com.inditex.technicalassesment.application.dto.response.PriceResponse;

public interface FindApplicablePriceUseCase {
    PriceResponse execute(FindPriceQuery query);
}
