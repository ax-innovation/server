package com.finapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_product")
@Getter
@NoArgsConstructor
public class FinanceProduct {

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

    @Column(name = "spcl_cnd", columnDefinition = "TEXT")
    private String spclCnd;

    @Column(name = "join_member", length = 500)
    private String joinMember;

    @Column(name = "join_deny", length = 1)
    private String joinDeny;

    @Column(name = "max_limit")
    private Long maxLimit;

    @Column(name = "dcls_strt_day", length = 8)
    private String dclsStrtDay;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
