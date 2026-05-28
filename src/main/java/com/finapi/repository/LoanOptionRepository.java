package com.finapi.repository;

import com.finapi.entity.LoanOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanOptionRepository extends JpaRepository<LoanOption, Long> {

    List<LoanOption> findByFinPrdtCdAndProductType(String finPrdtCd, String productType);
}
