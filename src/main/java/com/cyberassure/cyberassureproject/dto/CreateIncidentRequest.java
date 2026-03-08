package com.cyberassure.cyberassureproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateIncidentRequest {

    @NotBlank
    private String incidentType;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal estimatedLossAmount;

    @NotNull
    private Long subscriptionId;
}