package com.finapi.dto;

import lombok.Getter;
import java.util.List;

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
    private String       preferredBank;  // 청년도약계좌 희망 은행 (없으면 전체 비교)
}
