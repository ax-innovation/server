package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavingSimResult {
    private String bankName;        // ← 추가
    private long   totalDeposit;
    private long   baseInterest;
    private long   bestInterest;
    private long   govContribution;
    private long   baseFinalAmount;
    private long   bestFinalAmount;
    private String rateTypeNm;
}