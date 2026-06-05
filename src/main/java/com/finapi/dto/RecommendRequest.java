package com.finapi.dto;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class RecommendRequest {
    private int          age;
    private long         annualIncome;
    private long         monthlyDeposit;
    private int          termMonths;
    private long         loanAmount;
    private int          loanTermMonths;
    private String       purpose;
    private List<String> productTypes;
    private String       preferredBank;

    // 포트폴리오 모드
    private boolean            portfolioMode;  // true: 포트폴리오 / false: 일반
    private Map<String, Long>  allocation;     // 상품별 배분 금액
    // 포트폴리오 2단계: 사용자가 선택한 상품 코드
    // key: 상품 유형 / value: fin_prdt_cd (시중 상품) 또는 은행명 (청년도약계좌)
    private Map<String, String> selectedProducts;
}
