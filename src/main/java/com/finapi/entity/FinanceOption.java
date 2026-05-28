package com.finapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "finance_option")
@Getter
@NoArgsConstructor
public class FinanceOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_prdt_cd", nullable = false, length = 20)
    private String finPrdtCd;

    @Column(name = "product_type", length = 20)
    private String productType;

    @Column(name = "intr_rate_type", length = 5)
    private String intrRateType;

    @Column(name = "intr_rate_type_nm", length = 20)
    private String intrRateTypeNm;

    @Column(name = "save_trm")
    private Integer saveTrm;

    @Column(name = "intr_rate", precision = 5, scale = 2)
    private BigDecimal intrRate;

    @Column(name = "intr_rate2", precision = 5, scale = 2)
    private BigDecimal intrRate2;
}
