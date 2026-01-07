package com.inditex.technicalassesment.infrastructure.out.persistence.adapter;

import com.inditex.technicalassesment.domain.model.Price;
import com.inditex.technicalassesment.domain.port.out.PriceRepository;
import com.inditex.technicalassesment.infrastructure.out.persistence.entity.PriceEntity;
import com.inditex.technicalassesment.infrastructure.out.persistence.mapper.PriceMapper;
import com.inditex.technicalassesment.infrastructure.out.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PriceRepositoryImpl implements PriceRepository {

    private final PriceJpaRepository jpaRepository;

    @Override
    public List<Price> findApplicablePrices(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId
    ) {

        List<PriceEntity> entities = jpaRepository.findApplicablePrices(
                productId,
                brandId,
                applicationDate
        );

        return entities.stream()
                .map(PriceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
