package com.finapi.repository;

import com.finapi.entity.FinanceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FinanceProductRepository extends JpaRepository<FinanceProduct, Long> {

    @Query("""
        SELECT fp FROM FinanceProduct fp
        WHERE fp.productType = :productType
          AND fp.finPrdtCd IN (
              SELECT fo.finPrdtCd FROM FinanceOption fo
              WHERE fo.productType = :productType
                AND fo.saveTrm = :saveTrm
          )
    """)
    List<FinanceProduct> findTopByRate(
            @Param("productType") String productType,
            @Param("saveTrm") int saveTrm
    );
}
