package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.*;
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
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;

    // =========================
    // CUSTOMER FILES CLAIM
    // =========================
    public Claim fileClaim(CreateClaimRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        IncidentReport incident = incidentRepository
                .findById(request.getIncidentId())
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        PolicySubscription subscription = incident.getSubscription();

        // Must be policy owner
        if (!subscription.getCustomer().getUserId()
                .equals(customer.getUserId())) {
            throw new RuntimeException("Unauthorized claim attempt");
        }

        // Policy must be approved
        if (subscription.getStatus() != SubscriptionStatus.APPROVED) {
            throw new RuntimeException("Policy is not active");
        }

        // Policy must not be expired
        if (subscription.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Policy expired");
        }

        // Claim cannot exceed coverage
        BigDecimal coverageLimit = subscription
                .getPolicy()
                .getCoverageLimit();

        if (request.getClaimAmount().compareTo(coverageLimit) > 0) {
            throw new RuntimeException("Claim exceeds coverage limit");
        }

        Claim claim = Claim.builder()
                .claimAmount(request.getClaimAmount())
                .status(ClaimStatus.PENDING)
                .filedAt(LocalDateTime.now())
                .customer(customer)
                .incident(incident)
                .build();

        return claimRepository.save(claim);
    }

    // =========================
    // CLAIMS OFFICER REVIEW
    // =========================
    public Claim reviewClaim(Long claimId, ClaimDecisionRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new RuntimeException("Claim already reviewed");
        }

        if (request.getDecision().equalsIgnoreCase("APPROVED")) {

            claim.setStatus(ClaimStatus.APPROVED);

        } else if (request.getDecision().equalsIgnoreCase("REJECTED")) {

            claim.setStatus(ClaimStatus.REJECTED);
            claim.setRejectionReason(request.getRejectionReason());

        } else {
            throw new RuntimeException("Invalid decision");
        }

        claim.setReviewedBy(officer);
        claim.setReviewedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    // =========================
    // DASHBOARD - Pending Claims
    // =========================
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus(ClaimStatus.PENDING);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
}