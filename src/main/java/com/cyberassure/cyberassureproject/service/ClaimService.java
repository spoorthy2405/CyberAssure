package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.*;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.exception.*;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;

    // =========================
    // CUSTOMER FILES CLAIM
    // =========================
    public Claim fileClaim(CreateClaimRequest request) {

        // Spring Security stores the logged-in user's email in a "security context".
        // We extract it here — no need for the customer to send their email in the body.
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found. Please log in again."));

        // Verify the incident actually exists before allowing a claim to be processed
        IncidentReport incident = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No incident found with ID: " + request.getIncidentId() + ". Please report the incident first."));

        // Each incident is filed under a specific active policy subscription
        PolicySubscription subscription = incident.getSubscription();

        // SECURITY CHECK: Make sure the person filing the claim OWNS the policy
        if (!subscription.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new UnauthorizedAccessException(
                    "You are not authorised to file a claim against this incident. It belongs to a different policyholder.");
        }

        // BUSINESS RULE 1: Policy must be ACTIVE to raise a claim
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new PolicyNotActiveException(
                    "Your policy subscription is currently '" + subscription.getStatus() + "'. "
                    + "Only ACTIVE policies can be used to file claims.");
        }

        // BUSINESS RULE 2: Policy must not have expired
        if (subscription.getEndDate().isBefore(LocalDate.now())) {
            throw new ClaimValidationException(
                    "Your policy expired on " + subscription.getEndDate() + ". Claims cannot be filed after the policy end date.");
        }

        // BUSINESS RULE 3: Claim amount cannot exceed the policy's coverage limit
        BigDecimal coverageLimit = subscription.getPolicy().getCoverageLimit();
        if (request.getClaimAmount().compareTo(coverageLimit) > 0) {
            throw new ClaimValidationException(
                    "Claim amount ₹" + request.getClaimAmount() + " exceeds your policy coverage limit of ₹" + coverageLimit + ".");
        }

        // All checks passed — build and persist the claim
        Claim claim = Claim.builder()
                .claimAmount(request.getClaimAmount())
                .status(ClaimStatus.PENDING)
                .filedAt(LocalDateTime.now())
                .customer(customer)
                .incident(incident)
                .bankAccountNumber(request.getBankAccountNumber())
                .bankIfscCode(request.getBankIfscCode())
                .policeReportFiled(request.getPoliceReportFiled())
                .policeReportNumber(request.getPoliceReportNumber())
                .claimDescription(request.getClaimDescription())
                .build();

        return claimRepository.save(claim);
    }

    // =========================
    // CLAIMS OFFICER REVIEW
    // =========================
    public Claim reviewClaim(Long claimId, ClaimDecisionRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated claims officer not found."));

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("No claim found with ID: " + claimId));

        // Cannot re-review a claim that has already been finalized
        if (claim.getStatus() != ClaimStatus.PENDING
                && claim.getStatus() != ClaimStatus.UNDER_INVESTIGATION) {
            throw new ClaimValidationException(
                    "Claim #" + claimId + " has already been finalized with status '" + claim.getStatus() + "'. No further review is possible.");
        }

        // Apply the decision
        // This updates claim status (approved, rejected, or under investigation),
        // stores review details, and saves the updated claim to the database.
        String decision = request.getDecision();
        if (decision.equalsIgnoreCase("APPROVED")) {
            claim.setStatus(ClaimStatus.APPROVED);
        } else if (decision.equalsIgnoreCase("REJECTED")) {
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setRejectionReason(request.getRejectionReason());
        } else if (decision.equalsIgnoreCase("UNDER_INVESTIGATION")) {
            claim.setStatus(ClaimStatus.UNDER_INVESTIGATION);
            claim.setInvestigationNotes(request.getRejectionReason());
        } else {
            throw new InvalidDecisionException(
                    "Decision '" + decision + "' is not valid. Allowed values: APPROVED, REJECTED, UNDER_INVESTIGATION.");
        }

        claim.setReviewedBy(officer);
        claim.setReviewedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    // =========================
    // CLAIM ASSESSMENT ENGINE
    // =========================
    public ClaimAssessmentResult assessClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        IncidentReport incident = claim.getIncident();
        PolicySubscription subscription = incident.getSubscription();

        ClaimAssessmentResult result = new ClaimAssessmentResult();

        // 1. Coverage Limits & Remaining Coverage
        BigDecimal policyLimit = subscription.getPolicyLimit() != null ? subscription.getPolicyLimit() : subscription.getPolicy().getCoverageLimit();
        if (policyLimit == null) {
            policyLimit = BigDecimal.ZERO; // Bulletproof against bad dummy data
        }
        result.setPolicyCoverageLimit(policyLimit);

        // Calculate Vested (Pro-rated) Limit based on time elapsed
        LocalDate incidentDate = incident.getReportedAt() != null ? incident.getReportedAt().toLocalDate() : LocalDate.now();
        LocalDate startDate = subscription.getStartDate() != null ? subscription.getStartDate() : LocalDate.now().minusYears(10);
        
        Integer tenureMonths = subscription.getTenureMonths() != null ? subscription.getTenureMonths() : 12; // Default to 12 months
        long monthsElapsed = ChronoUnit.MONTHS.between(startDate, incidentDate);
        if (monthsElapsed < 0) {
            monthsElapsed = 0;
        }
        monthsElapsed += 1; // You pay premium for the current active month upfront
        if (monthsElapsed > tenureMonths) {
            monthsElapsed = tenureMonths; // Cap at total tenure
        }

        BigDecimal vestedLimit = policyLimit.multiply(new BigDecimal(monthsElapsed))
                .divide(new BigDecimal(tenureMonths), 2, java.math.RoundingMode.HALF_UP);
        
        result.setTenureMonths(tenureMonths);
        result.setMonthsElapsed(monthsElapsed);
        result.setVestedCoverageLimit(vestedLimit);

        List<Claim> paidClaims = claimRepository.findByIncidentSubscriptionAndStatus(subscription, ClaimStatus.SETTLED);
        BigDecimal sumPaid = paidClaims.stream()
                .map(c -> c.getSettlementAmount() != null ? c.getSettlementAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.setPreviousClaimsPaid(sumPaid);
        BigDecimal remaining = vestedLimit.subtract(sumPaid); // NOW based on Vested Limit
        result.setRemainingCoverage(remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO);

        // 2. Policy Validity Check
        LocalDate endDate = subscription.getEndDate() != null ? subscription.getEndDate() : LocalDate.now().plusYears(10);
        boolean dateValid = !incidentDate.isBefore(startDate) && !incidentDate.isAfter(endDate);
        result.setIncidentDateValid(dateValid);

        // 3. Covered Incident Type
        boolean covered = false;
        String incidentType = incident.getIncidentType() != null ? incident.getIncidentType() : "";
        if (subscription.getPolicy().getBenefits() != null) {
            for (String benefit : subscription.getPolicy().getBenefits()) {
                 if (benefit != null && benefit.toLowerCase().contains(incidentType.toLowerCase())) {
                     covered = true;
                     break;
                 }
            }
        }
        result.setCoveredIncidentType(!incidentType.isEmpty());

        // 4. Deductible Rule
        BigDecimal deductible = subscription.getDeductible() != null ? subscription.getDeductible() : BigDecimal.ZERO;
        result.setDeductible(deductible);

        // 5. Incident vs Claim Date (30 day limit)
        LocalDateTime reportedAt = incident.getReportedAt() != null ? incident.getReportedAt() : LocalDateTime.now();
        LocalDateTime filedAt = claim.getFiledAt() != null ? claim.getFiledAt() : LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(reportedAt, filedAt);
        result.setReportedWithin30Days(daysBetween <= 30);

        // 6. Evidence Verification
        boolean hasDocs = incident.getDocumentPaths() != null && !incident.getDocumentPaths().isEmpty();
        boolean hasPolice = claim.getPoliceReportFiled() != null && claim.getPoliceReportFiled();
        result.setEvidenceProvided(hasDocs || hasPolice);

        // 7. Sub-Limit Check
        BigDecimal claimAmount = claim.getClaimAmount() != null ? claim.getClaimAmount() : BigDecimal.ZERO;
        BigDecimal halfLimit = policyLimit.multiply(new BigDecimal("0.5"));
        result.setSubLimitApplied(claimAmount.compareTo(halfLimit) > 0);

        // 8. Previous Claims Frequency
        long claimsCount = claimRepository.countByCustomerAndStatus(claim.getCustomer(), ClaimStatus.SETTLED);
        result.setPreviousClaimsCount(claimsCount);

        // 9. Fraud Detection
        boolean highSuspicion = (claimsCount > 2 && !result.getEvidenceProvided()) || 
                                (claimAmount.compareTo(remaining) == 0 && remaining.compareTo(BigDecimal.ZERO) > 0);
        result.setFraudSuspicion(highSuspicion);

        // 10. Calculate Final Payout
        BigDecimal approvedBase = claimAmount.compareTo(result.getRemainingCoverage()) > 0 ? result.getRemainingCoverage() : claimAmount;
        
        // Subtract deductible
        BigDecimal finalPayable = approvedBase.subtract(deductible);
        
        // Must not be negative
        if (finalPayable.compareTo(BigDecimal.ZERO) < 0) {
            finalPayable = BigDecimal.ZERO;
        }

        // If date invalid or obvious fraud, recommend 0
        if (!result.getIncidentDateValid() || result.getFraudSuspicion()) {
            finalPayable = BigDecimal.ZERO;
        }

        result.setRecommendedPayout(finalPayable);
        result.setPayableAmount(finalPayable);

        return result;
    }

    // =========================
    // SETTLE CLAIM
    // =========================
    public Claim settleClaim(Long claimId, BigDecimal settlementAmount, String notes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated claims officer not found."));

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("No claim found with ID: " + claimId));

        if (claim.getStatus() != ClaimStatus.APPROVED
                && claim.getStatus() != ClaimStatus.UNDER_INVESTIGATION) {
            throw new ClaimValidationException(
                    "Claim #" + claimId + " cannot be settled because its current status is '" + claim.getStatus()
                    + "'. Only APPROVED or UNDER_INVESTIGATION claims can be settled.");
        }

        claim.setStatus(ClaimStatus.SETTLED);
        claim.setSettlementAmount(settlementAmount);
        claim.setInvestigationNotes(notes);
        claim.setReviewedBy(officer);
        claim.setReviewedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    // =========================
    // CLAIMS ASSIGNED TO ME (Claims Officer's queue)
    // =========================
    // Retrieves all claims assigned to a specific officer, ordered by most recently filed.
    public List<Claim> getAssignedClaims(String email) {
        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Claims officer not found with email: " + email));
        return claimRepository.findByAssignedOfficerOrderByFiledAtDesc(officer);
    }

    // =========================
    // DASHBOARD QUERIES
    // =========================
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus(ClaimStatus.PENDING);
    }

    // Returns PENDING + UNDER_INVESTIGATION + APPROVED claims for the active queue
    public List<Claim> getActiveClaims() {
        return claimRepository.findByStatusIn(
                List.of(ClaimStatus.PENDING, ClaimStatus.UNDER_INVESTIGATION, ClaimStatus.APPROVED));
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // Retrieves all claims for a specific customer sorted by most recent filing date
    public List<Claim> getClaimsByCustomer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return claimRepository.findByCustomerOrderByFiledAtDesc(user);
    }
}