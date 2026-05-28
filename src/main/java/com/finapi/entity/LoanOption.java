package com.finapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "loan_option")
@Getter
@NoArgsConstructor
public class LoanOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_prdt_cd", nullable = false, length = 20)
    private String finPrdtCd;

    @Column(name = "product_type", length = 20)
    private String productType;

    @Column(name = "mrtg_type", length = 10)
    private String mrtgType;

    @Column(name = "mrtg_type_nm", length = 50)
    private String mrtgTypeNm;

    @Column(name = "rpay_type", length = 10)
    private String rpayType;

    @Column(name = "rpay_type_nm", length = 50)
    private String rpayTypeNm;

    @Column(name = "lend_rate_type", length = 10)
    private String lendRateType;

    @Column(name = "lend_rate_type_nm", length = 50)
    private String lendRateTypeNm;

    @Column(name = "lend_rate_min", precision = 5, scale = 2)
    private BigDecimal lendRateMin;

    @Column(name = "lend_rate_max", precision = 5, scale = 2)
    private BigDecimal lendRateMax;

    @Column(name = "lend_rate_avg", precision = 5, scale = 2)
    private BigDecimal lendRateAvg;
}
