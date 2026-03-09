package com.cyberassure.cyberassureproject.dto;

import lombok.Data;

@Data
public class ApplyPolicyRequest {

    private Long policyId;

    // Risk Assessment Fields
    private Boolean firewallEnabled;
    private Boolean encryptionEnabled;
    private Boolean backupAvailable;
    private Boolean mfaEnabled;
    private Boolean iso27001Certified;
    private Boolean hasDataPrivacyOfficer;

    private Integer previousIncidentCount;
    private Integer employeeCount;
    private Double annualRevenue;
}
