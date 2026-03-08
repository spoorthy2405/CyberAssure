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
}