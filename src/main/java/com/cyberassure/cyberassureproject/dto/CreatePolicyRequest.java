package com.cyberassure.cyberassureproject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePolicyRequest {

    private String policyName;
    private BigDecimal coverageLimit;
    private BigDecimal basePremium;
    private Integer durationMonths;
}