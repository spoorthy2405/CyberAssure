package com.cyberassure.cyberassureproject.dto;

import lombok.Data;

@Data
public class UnderwriterDecisionRequest {

    private String decision; // APPROVED or REJECTED
    private String rejectionReason;
}