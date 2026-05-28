package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class LoanSimResult {
    private long   loanAmount;
    private int    termMonths;
    private double minRate;
    private double maxRate;
    private long   monthlyPaymentMin;
    private long   totalInterestMin;
    private long   totalPaymentMin;
    private long   monthlyPaymentMax;
    private long   totalInterestMax;
    private long   totalPaymentMax;
    private List<MonthlySchedule> schedule;
}
