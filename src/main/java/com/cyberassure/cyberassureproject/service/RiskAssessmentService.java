package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateRiskAssessmentRequest;
import com.cyberassure.cyberassureproject.entity.RiskAssessment;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.RiskAssessmentRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentRepository repository;
    private final UserRepository userRepository;

    public RiskAssessment submitRisk(CreateRiskAssessmentRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found. Please log in again."));

        int score = calculateRiskScore(request);
        String level = determineRiskLevel(score);

        RiskAssessment assessment = RiskAssessment.builder()
                .customer(customer)
                .firewallEnabled(request.getFirewallEnabled())
                .encryptionEnabled(request.getEncryptionEnabled())
                .backupAvailable(request.getBackupAvailable())
                .mfaEnabled(request.getMfaEnabled())
                .previousIncidentCount(request.getPreviousIncidentCount())
                .employeeCount(request.getEmployeeCount())
                .annualRevenue(request.getAnnualRevenue())
                .riskScore(score)
                .riskLevel(level)
                .build();

        return repository.save(assessment);
    }

    private int calculateRiskScore(CreateRiskAssessmentRequest request) {

        int score = 100;

        if (!request.getFirewallEnabled())
            score += 20;
        if (!request.getEncryptionEnabled())
            score += 20;
        if (!request.getBackupAvailable())
            score += 15;
        if (!request.getMfaEnabled())
            score += 15;

        score += request.getPreviousIncidentCount() * 10;

        return score;
    }

    private String determineRiskLevel(int score) {

        if (score < 120)
            return "LOW";
        if (score < 160)
            return "MEDIUM";
        return "HIGH";
    }

    public List<RiskAssessment> getAll() {
        return repository.findAll();
    }

}