package com.cyberassure.cyberassureproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cyber_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CyberPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private BigDecimal coverageLimit;

    @Column(nullable = false)
    private BigDecimal basePremium;

    @Column(nullable = false)
    private Integer durationMonths;

    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}