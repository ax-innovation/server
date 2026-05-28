package com.finapi.dto;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class RecommendRequest {
    private int          age;
    private long         annualIncome;
    private long         monthlyDeposit;     // 총 월 납입 가능 금액
    private int          termMonths;
    private long         loanAmount;
    private int          loanTermMonths;
    private String       purpose;
    private List<String> productTypes;
    private String       preferredBank;

    // 포트폴리오 배분 금액
    // key: 상품 유형 (예: "청년도약계좌", "적금", "정기예금")
    // value: 배분 금액 (원)
    // 예: {"청년도약계좌": 700000, "적금": 300000}
    private Map<String, Long> allocation;
}
