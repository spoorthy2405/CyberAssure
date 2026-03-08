package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateSubscriptionRequest;
import com.cyberassure.cyberassureproject.dto.UnderwriterDecisionRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicySubscriptionService {

    private final PolicySubscriptionRepository subscriptionRepository;
    private final CyberPolicyRepository policyRepository;
    private final RiskAssessmentRepository riskRepository;
    private final UserRepository userRepository;

    // ==============================
    // CUSTOMER SUBSCRIBE POLICY
    // ==============================

    public PolicySubscription subscribe(CreateSubscriptionRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CyberPolicy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        RiskAssessment latestRisk = riskRepository
                .findTopByCustomerOrderByCreatedAtDesc(customer)
                .orElseThrow(() -> new RuntimeException("No risk assessment found"));

        BigDecimal finalPremium = calculatePremium(
                policy.getBasePremium(),
                latestRisk.getRiskLevel());

        PolicySubscription subscription = PolicySubscription.builder()
                .policy(policy)
                .customer(customer)
                .riskAssessment(latestRisk)
                .calculatedPremium(finalPremium)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(policy.getDurationMonths()))
                .build();

        return subscriptionRepository.save(subscription);
    }

    // ==============================
    // PREMIUM CALCULATION
    // ==============================

    private BigDecimal calculatePremium(BigDecimal base, String riskLevel) {

        double multiplier = switch (riskLevel) {
            case "LOW" -> 1.0;
            case "MEDIUM" -> 1.3;
            case "HIGH" -> 1.7;
            default -> 1.0;
        };

        return base.multiply(BigDecimal.valueOf(multiplier));
    }

    // ==============================
    // UNDERWRITER FETCH QUEUE
    // ==============================

    public List<PolicySubscription> getAllSubscriptions() {

        return subscriptionRepository.findAll();

    }

    // ==============================
    // UNDERWRITER REVIEW DECISION
    // ==============================

    public PolicySubscription reviewSubscription(
            Long subscriptionId,
            UnderwriterDecisionRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User underwriter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PolicySubscription subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new RuntimeException("Already reviewed");
        }

        RiskAssessment risk = subscription.getRiskAssessment();
        CyberPolicy policy = subscription.getPolicy();

        // ===============================
        // RULE ENGINE
        // ===============================

        // Rule 1: High risk + high coverage → auto reject
        if (risk.getRiskLevel().equals("HIGH") &&
                policy.getCoverageLimit().compareTo(new BigDecimal("5000000")) > 0) {

            subscription.setStatus(SubscriptionStatus.REJECTED);
            subscription.setRejectionReason("High risk with excessive coverage");

        }

        // Rule 2: Premium too high → reject
        else if (subscription.getCalculatedPremium()
                .compareTo(new BigDecimal("200000")) > 0) {

            subscription.setStatus(SubscriptionStatus.REJECTED);
            subscription.setRejectionReason("Premium exceeds underwriting threshold");

        }

        // If no rule triggered → allow manual decision
        else {

            if (request.getDecision().equalsIgnoreCase("APPROVED")) {

                subscription.setStatus(SubscriptionStatus.APPROVED);

            }

            else if (request.getDecision().equalsIgnoreCase("REJECTED")) {

                subscription.setStatus(SubscriptionStatus.REJECTED);
                subscription.setRejectionReason(request.getRejectionReason());

            }

            else {

                throw new RuntimeException("Invalid decision");

            }

        }

        // Audit info
        subscription.setApprovedBy(underwriter);
        subscription.setApprovedAt(LocalDateTime.now());

        return subscriptionRepository.save(subscription);

    }

}