package com.cyberassure.cyberassureproject.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PolicyResponse {

    private Long id;
    private String policyName;
    private BigDecimal coverageLimit;
    private BigDecimal basePremium;
    private Integer durationMonths;
    private String sector;
    private String description;
    private java.util.List<String> benefits;
    private java.util.List<String> applicableTo;
    private Boolean isActive;
}