package com.cyberassure.cyberassureproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CyberPolicyRequest {

    @NotBlank
    private String policyName;

    @NotBlank
    private String sector;

    private String description;

    @NotNull
    private BigDecimal coverageLimit;

    @NotNull
    private BigDecimal basePremium;

    @NotNull
    private Integer durationMonths;

    private List<String> benefits;

    /** e.g. ["IT & SaaS", "Healthcare"] or ["All"] for universal */
    private List<String> applicableTo;
}
