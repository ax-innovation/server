package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavingSimResult {
    private String bankName;
    private long   totalDeposit;
    private long   baseInterest;
    private long   bestInterest;
    private long   govContribution;
    private long   baseFinalAmount;
    private long   bestFinalAmount;
    private String rateTypeNm;
    private String note;            // 청년도약계좌 부분납입 안내 문구
}
