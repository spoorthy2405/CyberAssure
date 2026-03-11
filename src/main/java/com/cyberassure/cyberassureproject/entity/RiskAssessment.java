package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with User (Customer)
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private Boolean firewallEnabled;
    private Boolean encryptionEnabled;
    private Boolean backupAvailable;
    private Boolean mfaEnabled;
    private Boolean iso27001Certified;
    private Boolean hasDataPrivacyOfficer;

    private Integer previousIncidentCount;
    private Integer employeeCount;
    private Double annualRevenue;

    private Integer riskScore;
    private String riskLevel;

    @Column(length = 2000)
    private String proofDocumentPaths;

    private LocalDateTime createdAt = LocalDateTime.now();
}