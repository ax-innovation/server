package com.finapi.service;

import com.finapi.dto.*;
import com.finapi.entity.*;
import com.finapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final FinanceProductRepository financeRepo;
    private final FinanceOptionRepository  financeOptRepo;
    private final LoanProductRepository    loanRepo;
    private final LoanOptionRepository     loanOptRepo;
    private final SimulationService        sim;

    public RecommendResponse recommend(RecommendRequest req) {
        List<RecommendItem> items = new ArrayList<>();

        for (String type : req.getProductTypes()) {
            switch (type) {
                case "적금"           -> items.addAll(recommendSaving(req));
                case "정기예금"       -> items.addAll(recommendDeposit(req));
                case "청년도약계좌"   -> items.addAll(recommendYouthLeap(req));
                case "주택담보대출"   -> items.addAll(recommendLoan(req, "주택담보대출"));
                case "전세자금대출"   -> items.addAll(recommendLoan(req, "전세자금대출"));
                case "개인신용대출"   -> items.addAll(recommendLoan(req, "개인신용대출"));
                case "디딤돌대출"     -> items.add(recommendDidimdol(req));
                case "버팀목전세자금" -> items.add(recommendButimok(req));
            }
        }

        items.sort(Comparator
                .comparing(RecommendItem::isEligible).reversed()
                .thenComparing(i -> {
                    if (i.getSavingSim() != null) return -i.getSavingSim().getBestFinalAmount();
                    if (i.getLoanSim()   != null) return  i.getLoanSim().getMonthlyPaymentMin();
                    return 0L;
                })
        );

        return RecommendResponse.builder().request(req).results(items).build();
    }

    // ── 적금 ──────────────────────────────────────────────────

    private List<RecommendItem> recommendSaving(RecommendRequest req) {
        return financeRepo.findTopByRate("적금", req.getTermMonths())
                .stream()
                .sorted((a, b) -> {
                    var optA = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            a.getFinPrdtCd(), "적금", req.getTermMonths());
                    var optB = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            b.getFinPrdtCd(), "적금", req.getTermMonths());
                    double rateA = optA.isEmpty() ? 0 : optA.get(0).getIntrRate2().doubleValue();
                    double rateB = optB.isEmpty() ? 0 : optB.get(0).getIntrRate2().doubleValue();
                    return Double.compare(rateB, rateA);
                })
                .limit(5)
                .map(p -> {
                    var opts = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            p.getFinPrdtCd(), "적금", req.getTermMonths());
                    if (opts.isEmpty()) return null;
                    FinanceOption opt = opts.get(0);
                    return RecommendItem.builder()
                            .productName(p.getFinPrdtNm()).institution(p.getKorCoNm())
                            .category("적금").purpose("저축")
                            .baseRate(opt.getIntrRate().doubleValue())
                            .bestRate(opt.getIntrRate2().doubleValue())
                            .termMonths(req.getTermMonths()).eligible(true)
                            .benefit(extractBenefit(p.getSpclCnd()))
                            .note(truncate(p.getSpclCnd(), 80))
                            .applyUrl("https://finlife.fss.or.kr")
                            .savingSim(sim.simulateSaving(
                                    req.getMonthlyDeposit(),
                                    opt.getIntrRate().doubleValue(),
                                    opt.getIntrRate2().doubleValue(),
                                    req.getTermMonths(), opt.getIntrRateType()))
                            .build();
                }).filter(i -> i != null).toList();
    }

    // ── 정기예금 ──────────────────────────────────────────────

    private List<RecommendItem> recommendDeposit(RecommendRequest req) {
        return financeRepo.findTopByRate("정기예금", req.getTermMonths())
                .stream()
                .sorted((a, b) -> {
                    var optA = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            a.getFinPrdtCd(), "정기예금", req.getTermMonths());
                    var optB = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            b.getFinPrdtCd(), "정기예금", req.getTermMonths());
                    double rateA = optA.isEmpty() ? 0 : optA.get(0).getIntrRate2().doubleValue();
                    double rateB = optB.isEmpty() ? 0 : optB.get(0).getIntrRate2().doubleValue();
                    return Double.compare(rateB, rateA);
                })
                .limit(5)
                .map(p -> {
                    var opts = financeOptRepo.findByFinPrdtCdAndProductTypeAndSaveTrm(
                            p.getFinPrdtCd(), "정기예금", req.getTermMonths());
                    if (opts.isEmpty()) return null;
                    FinanceOption opt = opts.get(0);
                    long principal    = req.getMonthlyDeposit();
                    double base       = opt.getIntrRate().doubleValue();
                    double best       = opt.getIntrRate2().doubleValue();
                    long baseInt      = Math.round(principal * base / 100.0 * req.getTermMonths() / 12.0);
                    long bestInt      = Math.round(principal * best / 100.0 * req.getTermMonths() / 12.0);
                    return RecommendItem.builder()
                            .productName(p.getFinPrdtNm()).institution(p.getKorCoNm())
                            .category("정기예금").purpose("저축")
                            .baseRate(base).bestRate(best)
                            .termMonths(req.getTermMonths()).eligible(true)
                            .applyUrl("https://finlife.fss.or.kr")
                            .savingSim(SavingSimResult.builder()
                                    .totalDeposit(principal)
                                    .baseInterest(baseInt).bestInterest(bestInt)
                                    .govContribution(0L)
                                    .baseFinalAmount(principal + baseInt)
                                    .bestFinalAmount(principal + bestInt)
                                    .rateTypeNm("단리").build())
                            .build();
                }).filter(i -> i != null).toList();
    }

    // ── 청년도약계좌 ──────────────────────────────────────────

    private List<RecommendItem> recommendYouthLeap(RecommendRequest req) {
        boolean ageOk    = req.getAge() >= 19 && req.getAge() <= 34;
        boolean incomeOk = req.getAnnualIncome() <= 75_000_000L;
        boolean eligible = ageOk && incomeOk;

        List<SavingSimResult> simResults = eligible
                ? sim.simulateYouthLeapByBank(
                req.getMonthlyDeposit(),
                req.getAnnualIncome(),
                req.getPreferredBank())
                : List.of();

        // 은행별로 각각 RecommendItem 생성
        if (eligible && !simResults.isEmpty()) {
            return simResults.stream().map(s ->
                    RecommendItem.builder()
                            .productName("청년도약계좌 (" + s.getBankName() + ")")
                            .institution(s.getBankName())
                            .category("자산형성").purpose("저축")
                            .baseRate(4.5).bestRate(6.0)
                            .termMonths(60).eligible(true)
                            .benefit("정부기여금 + 비과세")
                            .note("✅ 이자소득 비과세 / 정부기여금 지원")
                            .applyUrl("https://ylaccount.kinfa.or.kr")
                            .savingSim(s)
                            .build()
            ).toList();
        }

        // 가입 불가인 경우
        return List.of(RecommendItem.builder()
                .productName("청년도약계좌").institution("서민금융진흥원")
                .category("자산형성").purpose("저축")
                .baseRate(4.5).bestRate(6.0).termMonths(60).eligible(false)
                .note(!ageOk    ? "❌ 나이 조건 미충족 (만 19~34세)"
                        : !incomeOk ? "❌ 소득 조건 미충족 (총급여 7,500만원 이하)"
                          :             "")
                .applyUrl("https://ylaccount.kinfa.or.kr")
                .build());
    }

    // ── 시중 대출 ─────────────────────────────────────────────

    private List<RecommendItem> recommendLoan(RecommendRequest req, String productType) {
        int months = req.getLoanTermMonths() > 0 ? req.getLoanTermMonths() : defaultTerm(productType);

        return loanRepo.findTopByMinRate(productType)
                .stream()
                .sorted((a, b) -> {
                    var optA = loanOptRepo.findByFinPrdtCdAndProductType(a.getFinPrdtCd(), productType);
                    var optB = loanOptRepo.findByFinPrdtCdAndProductType(b.getFinPrdtCd(), productType);
                    double rateA = optA.isEmpty() ? 0 : (optA.get(0).getLendRateMin() != null ? optA.get(0).getLendRateMin().doubleValue() : 0);
                    double rateB = optB.isEmpty() ? 0 : (optB.get(0).getLendRateMin() != null ? optB.get(0).getLendRateMin().doubleValue() : 0);
                    return Double.compare(rateA, rateB);
                })
                .limit(5)
                .map(p -> {
                    var opts = loanOptRepo.findByFinPrdtCdAndProductType(p.getFinPrdtCd(), productType);
                    if (opts.isEmpty()) return null;

                    LoanOption opt = opts.stream()
                            .filter(o -> o.getRpayTypeNm() != null && o.getRpayTypeNm().contains("원리금균등"))
                            .findFirst().orElse(opts.get(0));

                    double minRate = opt.getLendRateMin() != null ? opt.getLendRateMin().doubleValue() : 0;
                    double maxRate = opt.getLendRateMax() != null ? opt.getLendRateMax().doubleValue() : minRate;

                    return RecommendItem.builder()
                            .productName(p.getFinPrdtNm()).institution(p.getKorCoNm())
                            .category(productType).purpose("대출")
                            .baseRate(minRate).bestRate(maxRate)
                            .termMonths(months).eligible(true)
                            .note(buildLoanNote(opt))
                            .applyUrl("https://finlife.fss.or.kr")
                            .loanSim(sim.simulateLoan(req.getLoanAmount(), minRate, maxRate, months))
                            .build();
                }).filter(i -> i != null).toList();
    }
    private int defaultTerm(String productType) {
        return switch (productType) {
            case "개인신용대출" -> 60;
            case "전세자금대출" -> 24;
            default             -> 240;
        };
    }

    private String buildLoanNote(LoanOption opt) {
        String rate  = opt.getLendRateTypeNm() != null ? opt.getLendRateTypeNm() : "";
        String repay = opt.getRpayTypeNm()     != null ? opt.getRpayTypeNm()     : "";
        if (rate.isEmpty() && repay.isEmpty()) return "";
        return String.format("금리 유형: %s / 상환 방식: %s", rate, repay);
    }

    // ── 정책 대출 ─────────────────────────────────────────────

    private RecommendItem recommendDidimdol(RecommendRequest req) {
        boolean eligible = req.getAnnualIncome() <= 60_000_000L;
        int     months   = req.getLoanTermMonths() > 0 ? req.getLoanTermMonths() : 240;
        return RecommendItem.builder()
                .productName("내집마련 디딤돌대출").institution("주택도시기금")
                .category("정책대출").purpose("대출")
                .baseRate(2.15).bestRate(3.00).termMonths(months).eligible(eligible)
                .benefit("저금리 정책 주담대")
                .note(eligible
                        ? "✅ 무주택 세대주 대상 / 5억원 이하 주택"
                        : "❌ 소득 조건 미충족 (부부합산 6,000만원 이하)")
                .applyUrl("https://enhuf.molit.go.kr")
                .loanSim(eligible ? sim.simulateLoan(req.getLoanAmount(), 2.15, 3.00, months) : null)
                .build();
    }

    private RecommendItem recommendButimok(RecommendRequest req) {
        boolean incomeOk = req.getAnnualIncome() <= 50_000_000L;
        boolean youth    = req.getAge() >= 19 && req.getAge() <= 34;
        int     months   = req.getLoanTermMonths() > 0 ? req.getLoanTermMonths() : 24;
        double  minRate  = youth ? 1.5 : 2.1;
        double  maxRate  = youth ? 2.1 : 2.9;
        return RecommendItem.builder()
                .productName(youth ? "청년전용 버팀목전세자금" : "버팀목전세자금")
                .institution("주택도시기금")
                .category("정책대출").purpose("대출")
                .baseRate(minRate).bestRate(maxRate).termMonths(months).eligible(incomeOk)
                .benefit(youth ? "청년 우대금리" : null)
                .note(!incomeOk ? "❌ 소득 조건 미충족 (5,000만원 이하)"
                        : youth     ? "✅ 청년 우대금리 (1.5~2.1%)"
                          :             "✅ 무주택 세대주 대상")
                .applyUrl("https://enhuf.molit.go.kr")
                .loanSim(incomeOk ? sim.simulateLoan(req.getLoanAmount(), minRate, maxRate, months) : null)
                .build();
    }

    // ── 유틸 ──────────────────────────────────────────────────

    private String extractBenefit(String s) {
        if (s == null) return null;
        if (s.contains("비과세"))  return "비과세";
        if (s.contains("청년"))    return "청년 우대";
        if (s.contains("주거래"))  return "주거래 우대";
        return null;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
