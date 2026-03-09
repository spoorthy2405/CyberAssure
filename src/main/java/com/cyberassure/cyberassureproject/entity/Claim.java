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
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal claimAmount;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String rejectionReason;

    private LocalDateTime filedAt;

    private LocalDateTime reviewedAt;

    @ManyToOne
    @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus", "companyWebsite",
            "companyAddress", "registrationNumber", "annualRevenue", "companySize", "phoneNumber" })
    private User customer;

    @ManyToOne
    @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus", "companyWebsite",
            "companyAddress", "registrationNumber", "annualRevenue", "companySize", "phoneNumber" })
    private User reviewedBy; // Claims Officer

    @ManyToOne
    @JsonIgnoreProperties({ "customer", "subscription", "documentPaths" })
    private IncidentReport incident;
}