package com.cyberassure.cyberassureproject.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UnderwriterDecisionRequest {

    private String decision; // APPROVED or REJECTED
    private String rejectionReason;
    private Integer riskScore; // 0-100 (underwriter's assessment)
    private BigDecimal coverageAmount; // ₹ amount of coverage granted
    private Integer tenureMonths; // policy tenure in months
    private String underwriterNotes; // optional notes

    // Advanced terms
    private BigDecimal fixedPremium; // Manual override for premium
    private BigDecimal policyLimit;
    private BigDecimal deductible;
    private String exclusions;
}