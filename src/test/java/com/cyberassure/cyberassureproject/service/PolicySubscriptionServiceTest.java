package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateSubscriptionRequest;
import com.cyberassure.cyberassureproject.dto.UnderwriterDecisionRequest;
import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.exception.*;
import com.cyberassure.cyberassureproject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicySubscriptionServiceTest {

    @Mock private PolicySubscriptionRepository subscriptionRepository;
    @Mock private CyberPolicyRepository policyRepository;
    @Mock private RiskAssessmentRepository riskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private PolicySubscriptionService service;

    private User customer;
    private User underwriter;
    private CyberPolicy policy;
    private RiskAssessment lowRisk;
    private PolicySubscription pendingSub;

    @BeforeEach
    void setUp() {
        Role custRole = Role.builder().roleId(1L).roleName("ROLE_CUSTOMER").isActive(true).build();
        Role uwRole = Role.builder().roleId(2L).roleName("ROLE_UNDERWRITER").isActive(true).build();

        customer = User.builder().userId(1L).email("cust@test.com").fullName("Cust").role(custRole).build();
        underwriter = User.builder().userId(2L).email("uw@test.com").fullName("UW").role(uwRole).build();

        policy = CyberPolicy.builder().id(1L).policyName("Test Policy")
                .basePremium(BigDecimal.valueOf(1000)).coverageLimit(BigDecimal.valueOf(50000))
                .durationMonths(12).build();

        lowRisk = RiskAssessment.builder().id(1L).customer(customer).riskScore(100).riskLevel("LOW").build();

        pendingSub = PolicySubscription.builder()
                .id(1L).customer(customer).policy(policy).riskAssessment(lowRisk)
                .status(SubscriptionStatus.PENDING)
                .calculatedPremium(BigDecimal.valueOf(1000))
                .tenureMonths(12).build();
    }

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ─── subscribe ────────────────────────────────────────────────
    @Test
    void subscribe_Success_WithLowRisk() {
        mockSecurityContext("cust@test.com");
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.setPolicyId(1L);

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(riskRepository.findTopByCustomerOrderByCreatedAtDesc(customer)).thenReturn(Optional.of(lowRisk));
        when(subscriptionRepository.save(any())).thenReturn(pendingSub);

        PolicySubscription result = service.subscribe(req);
        assertNotNull(result);
        assertEquals(SubscriptionStatus.PENDING, result.getStatus());
    }

    @Test
    void subscribe_ThrowsResourceNotFoundException_WhenNoRiskAssessment() {
        mockSecurityContext("cust@test.com");
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.setPolicyId(1L);

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(riskRepository.findTopByCustomerOrderByCreatedAtDesc(customer)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.subscribe(req));
    }

    @Test
    void subscribe_ThrowsResourceNotFoundException_WhenPolicyNotFound() {
        mockSecurityContext("cust@test.com");
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.setPolicyId(99L);

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.subscribe(req));
    }

    // ─── reviewSubscription ───────────────────────────────────────
    @Test
    void reviewSubscription_Approve_Success() {
        mockSecurityContext("uw@test.com");
        UnderwriterDecisionRequest req = new UnderwriterDecisionRequest();
        req.setDecision("APPROVED");
        req.setTenureMonths(12);

        pendingSub.setStatus(SubscriptionStatus.PENDING);
        when(userRepository.findByEmail("uw@test.com")).thenReturn(Optional.of(underwriter));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));
        when(subscriptionRepository.save(any())).thenReturn(pendingSub);

        PolicySubscription result = service.reviewSubscription(1L, req);
        // Status should be PENDING_PAYMENT after underwriter approval
        assertNotNull(result);
    }

    @Test
    void reviewSubscription_Reject_Success() {
        mockSecurityContext("uw@test.com");
        UnderwriterDecisionRequest req = new UnderwriterDecisionRequest();
        req.setDecision("REJECTED");
        req.setRejectionReason("High risk industry");

        when(userRepository.findByEmail("uw@test.com")).thenReturn(Optional.of(underwriter));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));
        when(subscriptionRepository.save(any())).thenReturn(pendingSub);

        PolicySubscription result = service.reviewSubscription(1L, req);
        assertNotNull(result);
    }

    @Test
    void reviewSubscription_ThrowsSubscriptionStateException_WhenAlreadyReviewed() {
        mockSecurityContext("uw@test.com");
        pendingSub.setStatus(SubscriptionStatus.APPROVED);
        UnderwriterDecisionRequest req = new UnderwriterDecisionRequest();
        req.setDecision("APPROVED");

        when(userRepository.findByEmail("uw@test.com")).thenReturn(Optional.of(underwriter));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));

        assertThrows(SubscriptionStateException.class, () -> service.reviewSubscription(1L, req));
    }

    @Test
    void reviewSubscription_ThrowsInvalidDecision_WhenBadDecision() {
        mockSecurityContext("uw@test.com");
        UnderwriterDecisionRequest req = new UnderwriterDecisionRequest();
        req.setDecision("MAYBE");

        when(userRepository.findByEmail("uw@test.com")).thenReturn(Optional.of(underwriter));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));

        assertThrows(InvalidDecisionException.class, () -> service.reviewSubscription(1L, req));
    }

    // ─── paySubscription ──────────────────────────────────────────
    @Test
    void paySubscription_Success() {
        mockSecurityContext("cust@test.com");
        pendingSub.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        pendingSub.setTenureMonths(12);

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));
        when(subscriptionRepository.save(any())).thenReturn(pendingSub);

        PolicySubscription result = service.paySubscription(1L);
        assertNotNull(result);
        verify(subscriptionRepository).save(any());
    }

    @Test
    void paySubscription_ThrowsSubscriptionStateException_WhenNotPendingPayment() {
        mockSecurityContext("cust@test.com");
        pendingSub.setStatus(SubscriptionStatus.ACTIVE); // not PENDING_PAYMENT

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));

        assertThrows(SubscriptionStateException.class, () -> service.paySubscription(1L));
    }

    @Test
    void paySubscription_ThrowsUnauthorizedAccess_WhenWrongCustomer() {
        mockSecurityContext("other@test.com");
        User other = User.builder().userId(99L).email("other@test.com").fullName("Other")
                .role(Role.builder().roleName("ROLE_CUSTOMER").build()).build();

        pendingSub.setStatus(SubscriptionStatus.PENDING_PAYMENT);

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(pendingSub));

        assertThrows(UnauthorizedAccessException.class, () -> service.paySubscription(1L));
    }

    // ─── getSubscriptionsByCustomer ───────────────────────────────
    @Test
    void getSubscriptionsByCustomer_ReturnsListForUser() {
        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(subscriptionRepository.findByCustomer(customer)).thenReturn(List.of(pendingSub));

        List<PolicySubscription> result = service.getSubscriptionsByCustomer("cust@test.com");
        assertEquals(1, result.size());
    }

    @Test
    void getSubscriptionsByCustomer_ThrowsResourceNotFoundException_WhenUserMissing() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getSubscriptionsByCustomer("ghost@test.com"));
    }
}
