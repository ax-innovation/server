package com.finapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_product")
@Getter
@NoArgsConstructor
public class LoanProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_prdt_cd", nullable = false, length = 20)
    private String finPrdtCd;

    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;

    @Column(name = "kor_co_nm", length = 100)
    private String korCoNm;

    @Column(name = "fin_prdt_nm", length = 200)
    private String finPrdtNm;

    @Column(name = "join_way", length = 200)
    private String joinWay;

    @Column(name = "loan_inci_expn", columnDefinition = "TEXT")
    private String loanInciExpn;

    @Column(name = "erly_rpay_fee", length = 300)
    private String erlyRpayFee;

    @Column(name = "dly_rate", length = 100)
    private String dlyRate;

    @Column(name = "loan_lmt", length = 300)
    private String loanLmt;

    @Column(name = "dcls_strt_day", length = 8)
    private String dclsStrtDay;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
