package com.cyberassure.cyberassureproject.dto;

import lombok.Data;

@Data
public class ClaimDecisionRequest {

    private String decision; // APPROVED or REJECTED
    private String rejectionReason;
}