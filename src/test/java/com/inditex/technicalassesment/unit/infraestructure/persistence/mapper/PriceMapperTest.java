package com.inditex.technicalassesment.unit.infraestructure.persistence.mapper;

import com.inditex.technicalassesment.domain.model.Currency;
import com.inditex.technicalassesment.domain.model.Price;
import com.inditex.technicalassesment.infrastructure.out.persistence.entity.BrandEntity;
import com.inditex.technicalassesment.infrastructure.out.persistence.entity.PriceEntity;
import com.inditex.technicalassesment.infrastructure.out.persistence.mapper.PriceMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class PriceMapperTest {

    @Test
    void shouldMapEntityToDomain() {

        BrandEntity brandEntity = BrandEntity.builder()
                .id(1L)
                .name("Zara")
                .build();

        PriceEntity entity = PriceEntity.builder()
                .id(1L)
                .brand(brandEntity)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .priceList(1)
                .productId(35455L)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        Price price = PriceMapper.toDomain(entity);

        assertThat(price).isNotNull();
        assertThat(price.brandId()).isEqualTo(1L);
        assertThat(price.productId()).isEqualTo(35455L);
        assertThat(price.priceList()).isEqualTo(1);
        assertThat(price.priority()).isEqualTo(0);
        assertThat(price.price()).isEqualByComparingTo("35.50");
        assertThat(price.currency()).isEqualTo(Currency.EUR);
        assertThat(price.startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0));
        assertThat(price.endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59));
    }

    @Test
    void shouldMapDomainToEntity() {

        Price price = new Price(
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                1,
                35455L,
                0,
                new BigDecimal("35.50"),
                Currency.EUR
        );

        PriceEntity entity = PriceMapper.toEntity(price);

        assertThat(entity).isNotNull();
        assertThat(entity.getBrand().getId()).isEqualTo(1L);
        assertThat(entity.getProductId()).isEqualTo(35455L);
        assertThat(entity.getPriceList()).isEqualTo(1);
        assertThat(entity.getPriority()).isEqualTo(0);
        assertThat(entity.getPrice()).isEqualByComparingTo("35.50");
        assertThat(entity.getCurrency()).isEqualTo("EUR");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {

        Price price = PriceMapper.toDomain(null);
        assertThat(price).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {

        PriceEntity entity = PriceMapper.toEntity(null);
        assertThat(entity).isNull();
    }
}
