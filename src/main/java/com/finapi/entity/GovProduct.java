package com.finapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gov_product")
@Getter
@NoArgsConstructor
public class GovProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true, length = 100)
    private String productId;

    @Column(name = "source", length = 20)
    private String source;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "institution", length = 200)
    private String institution;

    @Column(name = "target_desc", columnDefinition = "TEXT")
    private String targetDesc;

    @Column(name = "age_min")
    private Integer ageMin;

    @Column(name = "age_max")
    private Integer ageMax;

    @Column(name = "income_limit", length = 500)
    private String incomeLimit;

    @Column(name = "rate_info", length = 500)
    private String rateInfo;

    @Column(name = "limit_amount", length = 200)
    private String limitAmount;

    @Column(name = "period", length = 100)
    private String period;

    @Column(name = "benefit", columnDefinition = "TEXT")
    private String benefit;

    @Column(name = "apply_url", length = 300)
    private String applyUrl;

    @Column(name = "extra_json", columnDefinition = "JSON")
    private String extraJson;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
