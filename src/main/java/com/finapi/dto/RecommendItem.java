package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendItem {
    private String  productName;
    private String  institution;
    private String  finPrdtCd;       // 상품 코드 (포트폴리오 2단계 선택 시 사용)
    private String  category;
    private String  purpose;
    private Double  baseRate;
    private Double  bestRate;
    private Integer termMonths;
    private boolean eligible;
    private String  benefit;
    private String  note;
    private String  applyUrl;
    private Long    allocatedAmount;
    private SavingSimResult savingSim;
    private LoanSimResult   loanSim;
}
