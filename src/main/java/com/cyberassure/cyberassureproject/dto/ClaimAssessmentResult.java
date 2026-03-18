package com.cyberassure.cyberassureproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAssessmentResult {
    private BigDecimal policyCoverageLimit;
    private BigDecimal vestedCoverageLimit;
    private Integer tenureMonths;
    private Long monthsElapsed;
    private BigDecimal previousClaimsPaid;
    private BigDecimal remainingCoverage;
    private Boolean incidentDateValid;
    private Boolean coveredIncidentType;
    private BigDecimal deductible;
    private Boolean reportedWithin30Days;
    private Boolean evidenceProvided;
    private Boolean subLimitApplied;
    private Boolean fraudSuspicion;
    private Long previousClaimsCount;
    private BigDecimal recommendedPayout;
    private BigDecimal payableAmount;
}
