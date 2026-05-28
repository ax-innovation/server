package com.finapi.repository;

import com.finapi.entity.FinanceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinanceOptionRepository extends JpaRepository<FinanceOption, Long> {

    List<FinanceOption> findByFinPrdtCdAndProductTypeAndSaveTrm(
        String finPrdtCd, String productType, int saveTrm
    );
}
