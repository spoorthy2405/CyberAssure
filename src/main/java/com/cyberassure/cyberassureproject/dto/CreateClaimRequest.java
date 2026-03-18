package com.cyberassure.cyberassureproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateClaimRequest {

    @NotNull
    private Long incidentId;

    @NotNull
    private BigDecimal claimAmount;

    private String bankAccountNumber;
    private String bankIfscCode;
    private Boolean policeReportFiled;
    private String policeReportNumber;
    private String claimDescription;
}