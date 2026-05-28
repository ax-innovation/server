package com.finapi.repository;

import com.finapi.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {

    @Query("""
    SELECT lp FROM LoanProduct lp
    WHERE lp.productType = :productType
      AND lp.finPrdtCd IN (
          SELECT lo.finPrdtCd FROM LoanOption lo
          WHERE lo.productType = :productType
      )
""")
    List<LoanProduct> findTopByMinRate(@Param("productType") String productType);
}
