package com.inditex.technicalassesment.infrastructure.in.web.controller;

import com.inditex.technicalassesment.application.dto.query.FindPriceQuery;
import com.inditex.technicalassesment.application.dto.response.PriceResponse;
import com.inditex.technicalassesment.application.port.in.FindApplicablePriceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final FindApplicablePriceUseCase findApplicablePriceUseCase;

    @GetMapping("/applicable")
    public ResponseEntity<PriceResponse> findApplicablePrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId
    ) {
        FindPriceQuery query = FindPriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(productId)
                .brandId(brandId)
                .build();

        PriceResponse response = findApplicablePriceUseCase.execute(query);

        return ResponseEntity.ok(response);
    }
}
