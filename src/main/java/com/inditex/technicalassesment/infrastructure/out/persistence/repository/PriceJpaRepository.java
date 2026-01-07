package com.inditex.technicalassesment.infrastructure.out.persistence.repository;

import com.inditex.technicalassesment.infrastructure.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    @Query("""
        SELECT p FROM PriceEntity p
        JOIN FETCH p.brand b
        WHERE p.productId = :productId
        AND b.id = :brandId
        AND p.startDate <= :applicationDate
        AND p.endDate >= :applicationDate
        """)
    List<PriceEntity> findApplicablePrices(
            @Param("productId") Long productId,
            @Param("brandId") Long brandId,
            @Param("applicationDate") LocalDateTime applicationDate
    );
}