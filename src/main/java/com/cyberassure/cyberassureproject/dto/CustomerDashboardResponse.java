package com.cyberassure.cyberassureproject.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerDashboardResponse {

    private String customerName;
    private String customerCompany;
    private String customerIndustry;

    private BigDecimal totalCoverage;
    private Long activeClaimsCount;
    private BigDecimal claimedAmountYearly;
    private Integer daysToRenewal;
    private Integer latestRiskScore;
    private String latestRiskLevel;

    // Active Subscription Details for Hero Card
    private String activePolicyName;
    private String activePolicyId;
    private String subscriptionStatus;
    private String assignedUnderwriterName;
    private String policyStartDate;
    private String policyEndDate;

    // Underwriter Plan Details (set when APPROVED)
    private Integer riskScore;
    private String riskLevel;
    private BigDecimal calculatedPremium;
    private BigDecimal coverageAmount;
    private Integer tenureMonths;
    private String underwriterNotes;
    private BigDecimal policyLimit;
    private BigDecimal deductible;
    private String exclusions;

    // Rejection details (set when REJECTED)
    private String rejectionReason;
}
