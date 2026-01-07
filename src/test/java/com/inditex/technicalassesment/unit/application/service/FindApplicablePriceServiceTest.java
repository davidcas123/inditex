package com.inditex.technicalassesment.unit.application.service;

import com.inditex.technicalassesment.application.dto.query.FindPriceQuery;
import com.inditex.technicalassesment.application.dto.response.PriceResponse;
import com.inditex.technicalassesment.application.service.FindApplicablePriceService;
import com.inditex.technicalassesment.domain.exception.PriceNotFoundException;
import com.inditex.technicalassesment.domain.model.Currency;
import com.inditex.technicalassesment.domain.model.Price;
import com.inditex.technicalassesment.domain.port.out.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private FindApplicablePriceService service;

    private FindPriceQuery query;
    private LocalDateTime applicationDate;

    @BeforeEach
    void setUp() {
        applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        query = FindPriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(35455L)
                .brandId(1L)
                .build();
    }

    @Test
    @DisplayName("Should return price when only one price is applicable")
    void shouldReturnPriceWhenOnlyOnePriceIsApplicable() {
        Price price = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1, 35455L, 0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(List.of(price));

        PriceResponse response = service.execute(query);

        assertThat(response).isNotNull();
        assertThat(response.productId()).isEqualTo(35455L);
        assertThat(response.brandId()).isEqualTo(1L);
        assertThat(response.price()).isEqualByComparingTo("35.50");
        assertThat(response.priceList()).isEqualTo(1);

        verify(priceRepository).findApplicablePrices(applicationDate, 35455L, 1L);
    }

    @Test
    @DisplayName("Should return highest priority price when multiple prices are applicable")
    void shouldReturnHighestPriorityPriceWhenMultiplePricesApplicable() {
        Price lowPriority = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1, 35455L, 0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        Price highPriority = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 6, 14, 23, 59),
                2, 35455L, 1,
                new BigDecimal("25.45"),
                Currency.EUR
        );

        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(List.of(lowPriority, highPriority));

        PriceResponse response = service.execute(query);

        assertThat(response.price()).isEqualByComparingTo("25.45");
        assertThat(response.priceList()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenNoPriceFound() {
        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.execute(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found");

        verify(priceRepository).findApplicablePrices(applicationDate, 35455L, 1L);
    }

    @Test
    void shouldThrowExceptionWhenPricesExistButNoneApplicable() {
        Price priceOutOfRange = new Price(
                1L,
                LocalDateTime.of(2020, 6, 15, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1, 35455L, 0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(List.of(priceOutOfRange));

        assertThatThrownBy(() -> service.execute(query))
                .isInstanceOf(PriceNotFoundException.class);
    }
}
