package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private Integer previousIncidentCount;
    private Integer employeeCount;
    private Double annualRevenue;

    private Integer riskScore;
    private String riskLevel;

    private LocalDateTime createdAt = LocalDateTime.now();
}