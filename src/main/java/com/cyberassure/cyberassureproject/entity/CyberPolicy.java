package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cyber_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CyberPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private BigDecimal coverageLimit;

    @Column(nullable = false)
    private BigDecimal basePremium;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private String sector;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_benefits", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "benefit")
    private java.util.List<String> benefits;

    /**
     * Which company industry types this policy applies to.
     * Use "All" to make it visible to every customer regardless of sector.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "policy_applicable_to", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "industry_type")
    private java.util.List<String> applicableTo;

    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}