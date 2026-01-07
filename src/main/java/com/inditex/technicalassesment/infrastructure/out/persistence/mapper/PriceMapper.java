package com.inditex.technicalassesment.infrastructure.out.persistence.mapper;

import com.inditex.technicalassesment.domain.model.Currency;
import com.inditex.technicalassesment.domain.model.Price;
import com.inditex.technicalassesment.infrastructure.out.persistence.entity.BrandEntity;
import com.inditex.technicalassesment.infrastructure.out.persistence.entity.PriceEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PriceMapper {

    public static Price toDomain(PriceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Price(
                entity.getBrand().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriceList(),
                entity.getProductId(),
                entity.getPriority(),
                entity.getPrice(),
                Currency.fromCode(entity.getCurrency())
        );
    }

    public static PriceEntity toEntity(Price price) {
        if (price == null) {
            return null;
        }

        BrandEntity brand = BrandEntity.builder()
                .id(price.brandId())
                .build();

        return PriceEntity.builder()
                .brand(brand)
                .startDate(price.startDate())
                .endDate(price.endDate())
                .priceList(price.priceList())
                .productId(price.productId())
                .priority(price.priority())
                .price(price.price())
                .currency(price.currency().getCode())
                .build();
    }
}