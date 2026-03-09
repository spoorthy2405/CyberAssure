package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String incidentType; // Ransomware, Data Breach, Phishing etc

    private String description;

    private BigDecimal estimatedLossAmount;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private LocalDateTime reportedAt;

    @ManyToOne
    @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus" })
    private User customer;

    @ManyToOne
    @JsonIgnoreProperties({ "customer", "riskAssessment", "policy", "approvedBy" })
    private PolicySubscription subscription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "incident_documents", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "document_path")
    private java.util.List<String> documentPaths;
}