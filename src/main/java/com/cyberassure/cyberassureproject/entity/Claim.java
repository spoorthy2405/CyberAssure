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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Claim {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private BigDecimal claimAmount;

        private BigDecimal settlementAmount;

        @Enumerated(EnumType.STRING)
        private ClaimStatus status;

        private String rejectionReason;

        @Column(length = 2000)
        private String investigationNotes;

        // --- Additional Professional Claim Fields ---
        private String bankAccountNumber;
        private String bankIfscCode;
        private Boolean policeReportFiled;
        private String policeReportNumber;
        @Column(length = 2000)
        private String claimDescription;
        // --------------------------------------------

        private LocalDateTime filedAt;

        private LocalDateTime reviewedAt;

        @ManyToOne
        @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus", "companyWebsite",
                        "companyAddress", "registrationNumber", "annualRevenue", "companySize", "phoneNumber" })
        private User customer;

        @ManyToOne
        @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus", "companyWebsite",
                        "companyAddress", "registrationNumber", "annualRevenue", "companySize", "phoneNumber" })
        private User reviewedBy;

        @ManyToOne
        @JsonIgnoreProperties({ "passwordHash", "role", "createdAt", "updatedAt", "accountStatus", "companyWebsite",
                        "companyAddress", "registrationNumber", "annualRevenue", "companySize", "phoneNumber" })
        private User assignedOfficer;

        @ManyToOne
        private IncidentReport incident;
}