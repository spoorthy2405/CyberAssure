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
                latestRisk.getRiskLevel(),
                policy.getDurationMonths());

        PolicySubscription subscription = PolicySubscription.builder()
                .policy(policy)
                .customer(customer)
                .riskAssessment(latestRisk)
                .calculatedPremium(finalPremium)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(policy.getDurationMonths()))
                .tenureMonths(policy.getDurationMonths())
                .build();

        return subscriptionRepository.save(subscription);
    }

    // ==============================
    // PREMIUM FORMULA
    //
    // Premium = basePremium × riskMultiplier × (tenure / 12)
    // LOW → multiplier = 1.0
    // MEDIUM → multiplier = 1.3
    // HIGH → multiplier = 1.7
    // ==============================

    private BigDecimal calculatePremium(BigDecimal base, String riskLevel, int tenureMonths) {
        double multiplier = switch (riskLevel != null ? riskLevel : "LOW") {
            case "LOW" -> 1.0;
            case "MEDIUM" -> 1.3;
            case "HIGH" -> 1.7;
            default -> 1.0;
        };
        double tenureFactor = tenureMonths / 12.0;
        return base.multiply(BigDecimal.valueOf(multiplier)).multiply(BigDecimal.valueOf(tenureFactor));
    }

    // ==============================
    // COVERAGE FORMULA
    //
    // Coverage = policyMaxCoverage × coverageRatio
    // LOW → 100% of max coverage
    // MEDIUM → 80% of max coverage
    // HIGH → 60% of max coverage
    // ==============================

    private BigDecimal calculateCoverage(BigDecimal maxCoverage, String riskLevel) {
        double ratio = switch (riskLevel != null ? riskLevel : "LOW") {
            case "LOW" -> 1.0;
            case "MEDIUM" -> 0.80;
            case "HIGH" -> 0.60;
            default -> 1.0;
        };
        return maxCoverage.multiply(BigDecimal.valueOf(ratio));
    }

    // ==============================
    // UNDERWRITER FETCH QUEUE
    // ==============================

    public List<PolicySubscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<PolicySubscription> getAssignedToMe(String email) {
        User underwriter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return subscriptionRepository.findByAssignedUnderwriter(underwriter);
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

        // Step 1: Set Risk Score and derive Risk Level
        if (request.getRiskScore() != null) {
            risk.setRiskScore(request.getRiskScore());
            String riskLevel = request.getRiskScore() <= 40 ? "LOW"
                    : (request.getRiskScore() <= 70 ? "MEDIUM" : "HIGH");
            risk.setRiskLevel(riskLevel);
            riskRepository.save(risk);
        }

        // Step 2: Set Tenure
        int tenure = request.getTenureMonths() != null ? request.getTenureMonths() : policy.getDurationMonths();
        subscription.setTenureMonths(tenure);

        // Step 3: Calculate / accept Coverage Amount
        BigDecimal coverage = request.getCoverageAmount() != null
                ? request.getCoverageAmount()
                : calculateCoverage(policy.getCoverageLimit(), risk.getRiskLevel());
        subscription.setCoverageAmount(coverage);

        // Step 4: Calculate Premium
        BigDecimal premium = calculatePremium(policy.getBasePremium(), risk.getRiskLevel(), tenure);
        subscription.setCalculatedPremium(premium);

        // Step 5: Notes
        subscription.setUnderwriterNotes(request.getUnderwriterNotes());

        // Step 6: Apply decision
        if (request.getDecision().equalsIgnoreCase("APPROVED")) {
            subscription.setStatus(SubscriptionStatus.APPROVED);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusMonths(tenure));
        } else if (request.getDecision().equalsIgnoreCase("REJECTED")) {
            subscription.setStatus(SubscriptionStatus.REJECTED);
            subscription.setRejectionReason(request.getRejectionReason());
        } else {
            throw new RuntimeException("Invalid decision");
        }

        // Audit
        subscription.setApprovedBy(underwriter);
        subscription.setApprovedAt(LocalDateTime.now());

        return subscriptionRepository.save(subscription);
    }

    public List<PolicySubscription> getSubscriptionsByCustomer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return subscriptionRepository.findByCustomer(user);
    }
}