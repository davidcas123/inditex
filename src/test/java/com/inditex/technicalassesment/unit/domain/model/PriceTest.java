package com.inditex.technicalassesment.unit.domain.model;

import com.inditex.technicalassesment.domain.exception.InvalidPriceException;
import com.inditex.technicalassesment.domain.exception.PriceNotFoundException;
import com.inditex.technicalassesment.domain.model.Currency;
import com.inditex.technicalassesment.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    @DisplayName("Should create a valid Price with all required fields")
    void shouldCreateValidPrice() {

        Long brandId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        Integer priceList = 1;
        Long productId = 35455L;
        Integer priority = 0;
        BigDecimal price = new BigDecimal("35.50");
        Currency currency = Currency.EUR;

        Price result = new Price(
                brandId, startDate, endDate, priceList,
                productId, priority, price, currency
        );

        assertThat(result).isNotNull();
        assertThat(result.brandId()).isEqualTo(brandId);
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.price()).isEqualTo(price);
    }

    @Test
    @DisplayName("Should throw exception when brand ID is null")
    void shouldThrowExceptionWhenBrandIdIsNull() {
        assertThatThrownBy(() -> new Price(
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        ))
                .isInstanceOf(InvalidPriceException.class)
                .hasMessageContaining("Brand ID");
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when brand ID is invalid (zero or negative)")
    @ValueSource(longs = {0, -1, -100})
    void shouldThrowExceptionWhenBrandIdIsInvalid(Long invalidBrandId) {
        assertThatThrownBy(() -> new Price(
                invalidBrandId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        ))
                .isInstanceOf(InvalidPriceException.class);
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        LocalDateTime start = LocalDateTime.of(2020, 12, 31, 23, 59);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 0, 0);

        assertThatThrownBy(() -> new Price(
                1L, start, end, 1, 35455L, 0,
                new BigDecimal("35.50"), Currency.EUR
        ))
                .isInstanceOf(InvalidPriceException.class)
                .hasMessageContaining("Start date cannot be after end date");
    }

    @Test
    @DisplayName("Should throw exception when price amount is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> new Price(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                1,
                35455L,
                0,
                new BigDecimal("-10.00"),
                Currency.EUR
        ))
                .isInstanceOf(InvalidPriceException.class)
                .hasMessageContaining("Price must be non-negative");
    }

    @Test
    @DisplayName("Should return true when price is applicable at given date")
    void shouldReturnTrueWhenPriceIsApplicableAtDate() {

        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        Price price = new Price(
                1L, startDate, endDate, 1, 35455L, 0,
                new BigDecimal("35.50"), Currency.EUR
        );

        LocalDateTime testDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        boolean result = price.isApplicableAt(testDate);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when price is not applicable at given date")
    void shouldReturnFalseWhenPriceIsNotApplicableAtDate() {

        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        Price price = new Price(
                1L, startDate, endDate, 1, 35455L, 0,
                new BigDecimal("35.50"), Currency.EUR
        );

        LocalDateTime testDate = LocalDateTime.of(2021, 1, 1, 0, 0);

        boolean result = price.isApplicableAt(testDate);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when no price is applicable for the given date")
    void shouldThrowExceptionWhenNoPriceIsApplicable() {

        LocalDateTime date = LocalDateTime.of(2021, 1, 1, 0, 0);

        Price price = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1, 35455L, 0, new BigDecimal("35.50"), Currency.EUR
        );

        List<Price> prices = List.of(price);

        assertThatThrownBy(() -> Price.selectHighestPriority(prices, date))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found");
    }

    @Test
    @DisplayName("Should select price with highest priority when multiple prices are applicable")
    void shouldSelectHighestPriorityPrice() {

        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);

        Price lowPriority = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        Price highPriority = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                2,
                35455L,
                1,
                new BigDecimal("25.45"),
                Currency.EUR
        );

        List<Price> prices = List.of(lowPriority, highPriority);

        Price result = Price.selectHighestPriority(prices, date);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(highPriority);
        assertThat(result.priority()).isEqualTo(1);
        assertThat(result.price()).isEqualByComparingTo("25.45");
    }

    @Test
    @DisplayName("Should select the only applicable price when only one exists")
    void shouldSelectOnlyApplicablePrice() {

        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price onlyPrice = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        List<Price> prices = List.of(onlyPrice);

        Price result = Price.selectHighestPriority(prices, date);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(onlyPrice);
        assertThat(result.priority()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should throw exception when list is empty")
    void shouldThrowExceptionWhenListIsEmpty() {

        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);
        List<Price> emptyList = new ArrayList<>();

        assertThatThrownBy(() -> Price.selectHighestPriority(emptyList, date))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found for the given date")
                .hasMessageContaining(date.toString());
    }

    @Test
    @DisplayName("Should throw exception when no price is applicable for the date")
    void shouldThrowExceptionWhenNoPriceApplicable() {

        LocalDateTime queryDate = LocalDateTime.of(2021, 1, 1, 10, 0);

        Price priceOutOfRange = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        List<Price> prices = List.of(priceOutOfRange);

        assertThatThrownBy(() -> Price.selectHighestPriority(prices, queryDate))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found")
                .hasMessageContaining("2021-01-01T10:00");
    }

    @Test
    @DisplayName("Should select first price when both have same priority")
    void shouldSelectFirstPriceWithSamePriority() {

        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price price1 = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        Price price2 = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                2,
                35455L,
                0,
                new BigDecimal("30.00"),
                Currency.EUR
        );

        List<Price> prices = List.of(price1, price2);

        Price result = Price.selectHighestPriority(prices, date);

        assertThat(result).isNotNull();
        assertThat(result.priority()).isEqualTo(0);

        assertThat(result).isEqualTo(price1);
    }

}
