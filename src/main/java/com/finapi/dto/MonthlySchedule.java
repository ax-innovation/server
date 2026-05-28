package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlySchedule {
    private int  year;
    private long principalPaid;
    private long interestPaid;
    private long remainingBalance;
}
