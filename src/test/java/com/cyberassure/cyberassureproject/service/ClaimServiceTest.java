package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.ClaimDecisionRequest;
import com.cyberassure.cyberassureproject.dto.CreateClaimRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.exception.*;
import com.cyberassure.cyberassureproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock private ClaimRepository claimRepository;
    @Mock private IncidentReportRepository incidentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ClaimService claimService;

    private User customer;
    private User officer;
    private PolicySubscription activeSub;
    private CyberPolicy policy;
    private IncidentReport incident;
    private Claim pendingClaim;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().roleId(1L).roleName("ROLE_CUSTOMER").isActive(true).build();
        role.setRoleName("ROLE_CUSTOMER");

        customer = User.builder().userId(1L).email("cust@test.com").fullName("Cust").role(role).build();
        officer = User.builder().userId(2L).email("officer@test.com").fullName("Off").role(role).build();

        policy = CyberPolicy.builder().id(1L).policyName("Test Policy")
                .coverageLimit(BigDecimal.valueOf(10000)).durationMonths(12).build();

        activeSub = PolicySubscription.builder()
                .id(1L).customer(customer).policy(policy)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(LocalDate.now().plusMonths(11))
                .build();

        incident = IncidentReport.builder()
                .id(1L).customer(customer).subscription(activeSub)
                .incidentType("Ransomware").status(IncidentStatus.REPORTED)
                .reportedAt(LocalDateTime.now())
                .build();

        pendingClaim = Claim.builder()
                .id(1L).customer(customer).incident(incident)
                .status(ClaimStatus.PENDING).claimAmount(BigDecimal.valueOf(500))
                .filedAt(LocalDateTime.now())
                .build();
    }

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext secCtx = mock(SecurityContext.class);
        when(secCtx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(secCtx);
    }

    // ─── fileClaim ─────────────────────────────────────────────────
    @Test
    void fileClaim_Success() {
        mockSecurityContext("cust@test.com");
        CreateClaimRequest req = new CreateClaimRequest();
        req.setIncidentId(1L);
        req.setClaimAmount(BigDecimal.valueOf(500));

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(claimRepository.save(any())).thenReturn(pendingClaim);

        Claim result = claimService.fileClaim(req);

        assertNotNull(result);
        assertEquals(ClaimStatus.PENDING, result.getStatus());
    }

    @Test
    void fileClaim_ThrowsResourceNotFoundException_WhenIncidentNotFound() {
        mockSecurityContext("cust@test.com");
        CreateClaimRequest req = new CreateClaimRequest();
        req.setIncidentId(99L);
        req.setClaimAmount(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> claimService.fileClaim(req));
    }

    @Test
    void fileClaim_ThrowsUnauthorizedAccessException_WhenWrongCustomer() {
        mockSecurityContext("other@test.com");
        User other = User.builder().userId(99L).email("other@test.com").fullName("Other")
                .role(Role.builder().roleName("ROLE_CUSTOMER").build()).build();

        CreateClaimRequest req = new CreateClaimRequest();
        req.setIncidentId(1L);
        req.setClaimAmount(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThrows(UnauthorizedAccessException.class, () -> claimService.fileClaim(req));
    }

    @Test
    void fileClaim_ThrowsPolicyNotActiveException_WhenSubscriptionNotActive() {
        mockSecurityContext("cust@test.com");
        activeSub.setStatus(SubscriptionStatus.PENDING);

        CreateClaimRequest req = new CreateClaimRequest();
        req.setIncidentId(1L);
        req.setClaimAmount(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThrows(PolicyNotActiveException.class, () -> claimService.fileClaim(req));
    }

    @Test
    void fileClaim_ThrowsClaimValidationException_WhenAmountExceedsLimit() {
        mockSecurityContext("cust@test.com");

        CreateClaimRequest req = new CreateClaimRequest();
        req.setIncidentId(1L);
        req.setClaimAmount(BigDecimal.valueOf(99999)); // exceeds coverage limit of 10000

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThrows(ClaimValidationException.class, () -> claimService.fileClaim(req));
    }

    // ─── reviewClaim ───────────────────────────────────────────────
    @Test
    void reviewClaim_Approve_Success() {
        mockSecurityContext("officer@test.com");
        ClaimDecisionRequest req = new ClaimDecisionRequest();
        req.setDecision("APPROVED");

        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(pendingClaim));
        when(claimRepository.save(any())).thenReturn(pendingClaim);

        Claim result = claimService.reviewClaim(1L, req);
        assertEquals(ClaimStatus.APPROVED, result.getStatus());
    }

    @Test
    void reviewClaim_Reject_Success() {
        mockSecurityContext("officer@test.com");
        ClaimDecisionRequest req = new ClaimDecisionRequest();
        req.setDecision("REJECTED");
        req.setRejectionReason("Insufficient evidence");

        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(pendingClaim));
        when(claimRepository.save(any())).thenReturn(pendingClaim);

        Claim result = claimService.reviewClaim(1L, req);
        assertEquals(ClaimStatus.REJECTED, result.getStatus());
    }

    @Test
    void reviewClaim_ThrowsInvalidDecision_WhenBadDecisionValue() {
        mockSecurityContext("officer@test.com");
        ClaimDecisionRequest req = new ClaimDecisionRequest();
        req.setDecision("MAYBE");

        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(pendingClaim));

        assertThrows(InvalidDecisionException.class, () -> claimService.reviewClaim(1L, req));
    }

    @Test
    void reviewClaim_ThrowsClaimValidation_WhenAlreadyFinalized() {
        mockSecurityContext("officer@test.com");
        pendingClaim.setStatus(ClaimStatus.SETTLED);
        ClaimDecisionRequest req = new ClaimDecisionRequest();
        req.setDecision("APPROVED");

        when(userRepository.findByEmail("officer@test.com")).thenReturn(Optional.of(officer));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(pendingClaim));

        assertThrows(ClaimValidationException.class, () -> claimService.reviewClaim(1L, req));
    }

    // ─── getPendingClaims / getActiveClaims ───────────────────────
    @Test
    void getPendingClaims_ReturnsList() {
        when(claimRepository.findByStatus(ClaimStatus.PENDING)).thenReturn(List.of(pendingClaim));
        List<Claim> result = claimService.getPendingClaims();
        assertEquals(1, result.size());
    }

    @Test
    void getActiveClaims_ReturnsList() {
        when(claimRepository.findByStatusIn(any())).thenReturn(List.of(pendingClaim));
        List<Claim> result = claimService.getActiveClaims();
        assertEquals(1, result.size());
    }

    // ─── getClaimsByCustomer ──────────────────────────────────────
    @Test
    void getClaimsByCustomer_ReturnsClaimsForUser() {
        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(claimRepository.findByCustomerOrderByFiledAtDesc(customer)).thenReturn(List.of(pendingClaim));

        List<Claim> result = claimService.getClaimsByCustomer("cust@test.com");
        assertEquals(1, result.size());
    }

    @Test
    void getClaimsByCustomer_ThrowsResourceNotFoundException_WhenUserMissing() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> claimService.getClaimsByCustomer("ghost@test.com"));
    }
}
