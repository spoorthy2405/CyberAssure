package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CyberPolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final PolicySubscriptionRepository policySubscriptionRepository;
    private final ClaimRepository claimRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // ============================================================
    // DASHBOARD STATS — fully null-safe
    // ============================================================
    public CustomerDashboardResponse getDashboardStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for email: " + userEmail));

        // Defaults
        BigDecimal totalCoverage = BigDecimal.ZERO;
        String policyName = "No Active Policy";
        String policyId = "—";
        String assignedUnderwriter = "Pending";
        Integer daysToRenewal = null;
        String subscriptionStatus = "NONE";
        String policyStartDate = null;
        String policyEndDate = null;
        // Defaults for underwriter plan fields
        Integer uwRiskScore = null;
        String uwRiskLevel = null;
        BigDecimal uwPremium = null;
        BigDecimal uwCoverage = null;
        Integer uwTenure = null;
        String uwNotes = null;
        BigDecimal uwPolicyLimit = null;
        BigDecimal uwDeductible = null;
        String uwExclusions = null;
        String rejectionReason = null;
        try {
            // First check if customer has any ACTIVE policy
            List<PolicySubscription> active = policySubscriptionRepository
                    .findByCustomerAndStatus(user, SubscriptionStatus.ACTIVE);

            if (active.isEmpty()) {
                // Fallback to APPROVED if not yet active
                active = policySubscriptionRepository
                        .findByCustomerAndStatus(user, SubscriptionStatus.APPROVED);
            }

            if (!active.isEmpty()) {
                PolicySubscription sub = active.get(0);
                subscriptionStatus = sub.getStatus().name(); // Will be ACTIVE or APPROVED

                if (sub.getPolicy() != null) {
                    totalCoverage = safe(sub.getCoverageAmount() != null ? sub.getCoverageAmount()
                            : sub.getPolicy().getCoverageLimit());
                    policyName = sub.getPolicy().getPolicyName();
                    policyId = "CA-" + sub.getId();
                }

                if (sub.getApprovedBy() != null && sub.getApprovedBy().getFullName() != null) {
                    assignedUnderwriter = sub.getApprovedBy().getFullName();
                }

                if (sub.getStartDate() != null) {
                    policyStartDate = sub.getStartDate().format(DATE_FMT);
                }

                if (sub.getEndDate() != null) {
                    policyEndDate = sub.getEndDate().format(DATE_FMT);
                    daysToRenewal = (int) java.time.temporal.ChronoUnit.DAYS
                            .between(LocalDate.now(), sub.getEndDate());
                }

                // Populate underwriter plan details
                uwRiskScore = sub.getRiskScore();
                if (uwRiskScore != null) {
                    uwRiskLevel = uwRiskScore <= 40 ? "LOW" : (uwRiskScore <= 70 ? "MEDIUM" : "HIGH");
                }
                uwPremium = sub.getCalculatedPremium();
                uwCoverage = sub.getCoverageAmount();
                uwTenure = sub.getTenureMonths();
                uwNotes = sub.getUnderwriterNotes();
                uwPolicyLimit = sub.getPolicyLimit();
                uwDeductible = sub.getDeductible();
                uwExclusions = sub.getExclusions();

            } else {
                List<PolicySubscription> rejected = policySubscriptionRepository
                        .findByCustomerAndStatus(user, SubscriptionStatus.REJECTED);
                if (!rejected.isEmpty()) {
                    PolicySubscription sub = rejected.get(0);
                    subscriptionStatus = "REJECTED";
                    if (sub.getPolicy() != null) {
                        policyName = sub.getPolicy().getPolicyName();
                        policyId = "CA-" + sub.getId();
                    }
                    rejectionReason = sub.getRejectionReason();
                    uwRiskScore = sub.getRiskScore();
                } else {
                    // pending?
                    List<PolicySubscription> pending = policySubscriptionRepository
                            .findByCustomerAndStatus(user, SubscriptionStatus.PENDING);
                    if (!pending.isEmpty()) {
                        subscriptionStatus = "PENDING";
                        PolicySubscription sub = pending.get(0);
                        if (sub.getPolicy() != null) {
                            policyName = sub.getPolicy().getPolicyName() + " (Pending)";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            subscriptionStatus = "ERROR: " + e.getMessage();
        }

        // Risk
        Integer riskScore = uwRiskScore;
        String riskLevel = uwRiskLevel != null ? uwRiskLevel : "UNKNOWN";
        try {
            Optional<RiskAssessment> latestRisk = riskAssessmentRepository.findTopByCustomerOrderByCreatedAtDesc(user);
            if (latestRisk.isPresent()) {
                if (riskScore == null)
                    riskScore = latestRisk.get().getRiskScore();
                if (uwRiskLevel == null)
                    riskLevel = latestRisk.get().getRiskLevel() != null
                            ? latestRisk.get().getRiskLevel()
                            : "UNKNOWN";
            }
        } catch (Exception e) {
            // fall through
        }

        // Claims
        long activeClaims = 0;
        BigDecimal claimedYearly = BigDecimal.ZERO;
        try {
            activeClaims = claimRepository.countByCustomerAndStatus(user, ClaimStatus.PENDING);
            LocalDateTime yearStart = LocalDateTime.now().withDayOfYear(1)
                    .withHour(0).withMinute(0).withSecond(0);
            BigDecimal sum = claimRepository
                    .sumClaimAmountByCustomerAndStatusSince(user, ClaimStatus.APPROVED, yearStart);
            if (sum != null)
                claimedYearly = sum;
        } catch (Exception e) {
            // fall through
        }

        return CustomerDashboardResponse.builder()
                .customerName(user.getFullName())
                .customerCompany(user.getCompanyName())
                .customerIndustry(user.getIndustry())
                .totalCoverage(totalCoverage)
                .activeClaimsCount(activeClaims)
                .claimedAmountYearly(claimedYearly)
                .daysToRenewal(daysToRenewal)
                .latestRiskScore(riskScore)
                .latestRiskLevel(riskLevel)
                .activePolicyName(policyName)
                .activePolicyId(policyId)
                .subscriptionStatus(subscriptionStatus)
                .assignedUnderwriterName(assignedUnderwriter)
                .policyStartDate(policyStartDate)
                .policyEndDate(policyEndDate)
                .riskScore(uwRiskScore)
                .riskLevel(uwRiskLevel)
                .calculatedPremium(uwPremium)
                .coverageAmount(uwCoverage)
                .tenureMonths(uwTenure)
                .underwriterNotes(uwNotes)
                .policyLimit(uwPolicyLimit)
                .deductible(uwDeductible)
                .exclusions(uwExclusions)
                .rejectionReason(rejectionReason)
                .build();
    }

    // ============================================================
    // RECOMMENDED POLICIES — filtered strictly by customer industry
    // ============================================================
    public List<PolicyResponse> getRecommendedPolicies(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for email: " + userEmail));

        String userIndustry = user.getIndustry() != null ? user.getIndustry().trim() : "";

        return policyRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .filter(p -> {
                    // Strictly match the policy sector with the customer's industry
                    return p.getSector() != null && p.getSector().equalsIgnoreCase(userIndustry);
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // APPLY FOR POLICY
    // ============================================================
    public void applyForPolicy(ApplyPolicyRequest request, String userEmail,
            java.util.List<org.springframework.web.multipart.MultipartFile> proofFiles) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for email: " + userEmail));

        CyberPolicy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No policy found with ID: " + request.getPolicyId()));

        // Save proof documents
        String savedPaths = "";
        if (proofFiles != null && !proofFiles.isEmpty()) {
            java.util.List<String> paths = new java.util.ArrayList<>();
            for (org.springframework.web.multipart.MultipartFile file : proofFiles) {
                if (file.isEmpty())
                    continue;
                try {
                    String uploadDir = "uploads/proofs/" + user.getUserId();
                    java.nio.file.Path dir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
                    if (!java.nio.file.Files.exists(dir)) {
                        java.nio.file.Files.createDirectories(dir);
                    }
                    String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    java.nio.file.Path target = dir.resolve(filename);
                    java.nio.file.Files.copy(file.getInputStream(), target,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    paths.add(uploadDir + "/" + filename); // Store relative path for frontend parsing consistency
                } catch (Exception e) {
                    throw new ResourceNotFoundException("Failed to save proof document '" + file.getOriginalFilename() + "': " + e.getMessage());
                }
            }
            savedPaths = String.join(",", paths);
        }

        RiskAssessment assessment = RiskAssessment.builder()
                .customer(user)
                .firewallEnabled(request.getFirewallEnabled())
                .encryptionEnabled(request.getEncryptionEnabled())
                .backupAvailable(request.getBackupAvailable())
                .mfaEnabled(request.getMfaEnabled())
                .iso27001Certified(request.getIso27001Certified())
                .hasDataPrivacyOfficer(request.getHasDataPrivacyOfficer())
                .previousIncidentCount(request.getPreviousIncidentCount())
                .employeeCount(request.getEmployeeCount())
                .annualRevenue(request.getAnnualRevenue())
                // Risk score and level will be determined by the underwriter later
                .riskScore(null)
                .riskLevel("PENDING")
                .proofDocumentPaths(savedPaths.isEmpty() ? null : savedPaths)
                .createdAt(LocalDateTime.now())
                .build();
        assessment = riskAssessmentRepository.save(assessment);

        PolicySubscription subscription = PolicySubscription.builder()
                .customer(user)
                .policy(policy)
                .riskAssessment(assessment)
                .status(SubscriptionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        policySubscriptionRepository.save(subscription);
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private PolicyResponse mapToResponse(CyberPolicy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyName(policy.getPolicyName())
                .coverageLimit(policy.getCoverageLimit())
                .basePremium(policy.getBasePremium())
                .durationMonths(policy.getDurationMonths())
                .sector(policy.getSector())
                .description(policy.getDescription())
                .benefits(policy.getBenefits())
                .applicableTo(policy.getApplicableTo())
                .isActive(policy.getIsActive())
                .build();
    }
}
