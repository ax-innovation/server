package com.finapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finapi.dto.*;
import com.finapi.entity.GovProduct;
import com.finapi.repository.GovProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final GovProductRepository govRepo;
    private final ObjectMapper         objectMapper;

    // ── 저축 시뮬레이션 ───────────────────────────────────────

    public SavingSimResult simulateSaving(
            long monthly, double baseRate, double bestRate,
            int months, String rateType) {

        long totalDeposit = monthly * months;
        long baseInterest = calcSavingInterest(monthly, baseRate, months, rateType);
        long bestInterest = calcSavingInterest(monthly, bestRate, months, rateType);

        return SavingSimResult.builder()
                .totalDeposit(totalDeposit)
                .baseInterest(baseInterest)
                .bestInterest(bestInterest)
                .govContribution(0L)
                .baseFinalAmount(totalDeposit + baseInterest)
                .bestFinalAmount(totalDeposit + bestInterest)
                .rateTypeNm("M".equals(rateType) ? "월복리" : "단리")
                .build();
    }

    // ── 청년도약계좌 시뮬레이션 (은행별) ─────────────────────

    public List<SavingSimResult> simulateYouthLeapByBank(
            long monthly, long annualIncome, String preferredBank) {

        GovProduct product = govRepo
                .findByProductId("static_youth_leap_account")
                .orElseThrow();

        Map<String, Map<String, Double>> ratesByBank = parseRatesByBank(product.getExtraJson());
        List<SavingSimResult> results = new ArrayList<>();

        if (preferredBank != null && !preferredBank.isEmpty()) {
            Map<String, Double> rates = ratesByBank.get(preferredBank);
            if (rates != null) {
                results.add(calcYouthLeap(monthly, annualIncome,
                        rates.get("base"), rates.get("best"), preferredBank));
            }
            return results;
        }

        for (Map.Entry<String, Map<String, Double>> entry : ratesByBank.entrySet()) {
            results.add(calcYouthLeap(monthly, annualIncome,
                    entry.getValue().get("base"),
                    entry.getValue().get("best"),
                    entry.getKey()));
        }

        results.sort(Comparator.comparingLong(SavingSimResult::getBestFinalAmount).reversed());
        return results;
    }

    private SavingSimResult calcYouthLeap(
            long monthly, long annualIncome,
            double baseRate, double bestRate, String bankName) {

        int  months       = 60;
        long capped       = Math.min(monthly, 700_000L);
        long monthlyGov   = calcMonthlyGov(annualIncome, capped);
        long totalGov     = monthlyGov * months;
        long totalDeposit = capped * months;
        long baseInterest = calcSavingInterest(capped, baseRate, months, "S");
        long bestInterest = calcSavingInterest(capped, bestRate, months, "S");

        return SavingSimResult.builder()
                .bankName(bankName)
                .totalDeposit(totalDeposit)
                .baseInterest(baseInterest)
                .bestInterest(bestInterest)
                .govContribution(totalGov)
                .baseFinalAmount(totalDeposit + baseInterest + totalGov)
                .bestFinalAmount(totalDeposit + bestInterest + totalGov)
                .rateTypeNm("단리")
                .build();
    }

    private Map<String, Map<String, Double>> parseRatesByBank(String extraJson) {
        try {
            JsonNode root  = objectMapper.readTree(extraJson);
            JsonNode rates = root.get("rates_by_bank");
            Map<String, Map<String, Double>> result = new LinkedHashMap<>();
            rates.fields().forEachRemaining(entry -> {
                Map<String, Double> bankRates = new LinkedHashMap<>();
                bankRates.put("base", entry.getValue().get("base").asDouble());
                bankRates.put("best", entry.getValue().get("best").asDouble());
                result.put(entry.getKey(), bankRates);
            });
            return result;
        } catch (Exception e) {
            return Map.of("기본", Map.of("base", 4.5, "best", 6.0));
        }
    }

    // ── 대출 시뮬레이션 ───────────────────────────────────────

    public LoanSimResult simulateLoan(long loanAmount, double minRate, double maxRate, int months) {
        LoanCalc minCalc = calcLoan(loanAmount, minRate, months);
        LoanCalc maxCalc = calcLoan(loanAmount, maxRate, months);

        return LoanSimResult.builder()
                .loanAmount(loanAmount)
                .termMonths(months)
                .minRate(minRate).maxRate(maxRate)
                .monthlyPaymentMin(minCalc.monthlyPayment)
                .totalInterestMin(minCalc.totalInterest)
                .totalPaymentMin(minCalc.totalPayment)
                .monthlyPaymentMax(maxCalc.monthlyPayment)
                .totalInterestMax(maxCalc.totalInterest)
                .totalPaymentMax(maxCalc.totalPayment)
                .schedule(buildSchedule(loanAmount, minRate, months))
                .build();
    }

    private List<MonthlySchedule> buildSchedule(long principal, double annualRate, int months) {
        double r          = annualRate / 100.0 / 12.0;
        double monthlyPmt = r == 0
                ? (double) principal / months
                : principal * r * Math.pow(1 + r, months) / (Math.pow(1 + r, months) - 1);

        List<MonthlySchedule> result = new ArrayList<>();
        long remaining = principal;
        int  totalYears = (int) Math.ceil(months / 12.0);

        for (int year = 1; year <= totalYears; year++) {
            int  startMonth      = (year - 1) * 12 + 1;
            int  endMonth        = Math.min(year * 12, months);
            long yearlyPrincipal = 0;
            long yearlyInterest  = 0;

            for (int m = startMonth; m <= endMonth; m++) {
                long interest   = Math.round(remaining * r);
                long principal_ = m == months ? remaining : Math.round(monthlyPmt) - interest;
                yearlyInterest  += interest;
                yearlyPrincipal += principal_;
                remaining       -= principal_;
                if (remaining < 0) remaining = 0;
            }

            result.add(MonthlySchedule.builder()
                    .year(year)
                    .principalPaid(yearlyPrincipal)
                    .interestPaid(yearlyInterest)
                    .remainingBalance(remaining)
                    .build());
        }
        return result;
    }

    // ── 내부 계산 유틸 ─────────────────────────────────────────

    public long calcSavingInterest(long monthly, double rate, int months, String rateType) {
        if ("M".equals(rateType)) {
            double r  = rate / 100.0 / 12.0;
            double fv = monthly * (Math.pow(1 + r, months) - 1) / r;
            return Math.round(fv) - monthly * (long) months;
        }
        double r = rate / 100.0 / 12.0;
        return Math.round(monthly * r * months * (months + 1) / 2.0);
    }

    private record LoanCalc(long monthlyPayment, long totalInterest, long totalPayment) {}

    private LoanCalc calcLoan(long principal, double annualRate, int months) {
        if (annualRate == 0) {
            long mp = Math.round((double) principal / months);
            return new LoanCalc(mp, 0L, principal);
        }
        double r     = annualRate / 100.0 / 12.0;
        long   mp    = Math.round(principal * r * Math.pow(1 + r, months) / (Math.pow(1 + r, months) - 1));
        long   total = mp * months;
        return new LoanCalc(mp, total - principal, total);
    }

    private long calcMonthlyGov(long income, long monthly) {
        double rate  = govRate(income);
        long   limit = govLimit(income);
        return Math.min(Math.round(monthly * rate / 100.0), limit);
    }

    private double govRate(long income) {
        if      (income <= 24_000_000) return 6.0;
        else if (income <= 36_000_000) return 4.6;
        else if (income <= 48_000_000) return 3.7;
        else if (income <= 60_000_000) return 3.0;
        return 0.0;
    }

    private long govLimit(long income) {
        if      (income <= 24_000_000) return 400_000L;
        else if (income <= 36_000_000) return 500_000L;
        else if (income <= 48_000_000) return 600_000L;
        else if (income <= 60_000_000) return 700_000L;
        return 0L;
    }
}