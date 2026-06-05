package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class RecommendResponse {
    private RecommendRequest    request;
    private List<RecommendItem> results;

    // 포트폴리오 합산 결과
    private Long totalFinalAmount;      // 전체 만기 수령액 합산
    private Long totalDeposit;          // 전체 납입액 합산
    private Long totalInterest;         // 전체 이자 합산
    private Long totalGovContribution;  // 전체 정부기여금 합산
    private boolean isPortfolio;        // 포트폴리오 모드 여부
}
