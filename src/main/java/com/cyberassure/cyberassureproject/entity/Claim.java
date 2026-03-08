package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private User customer;

    @ManyToOne
    private User reviewedBy;   // Claims Officer

    @ManyToOne
    private IncidentReport incident;
}