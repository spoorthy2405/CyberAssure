package com.cyberassure.cyberassureproject.dto;

import lombok.Data;

@Data
public class CreateRiskAssessmentRequest {

    private Boolean firewallEnabled;
    private Boolean encryptionEnabled;
    private Boolean backupAvailable;
    private Boolean mfaEnabled;

    private Integer previousIncidentCount;
    private Integer employeeCount;
    private Double annualRevenue;
}