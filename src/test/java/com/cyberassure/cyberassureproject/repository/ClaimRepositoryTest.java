package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClaimRepositoryTest {

    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CyberPolicyRepository policyRepository;

    @Autowired
    RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    PolicySubscriptionRepository subscriptionRepository;

    @Autowired
    IncidentReportRepository incidentReportRepository;

    private User testCustomer;
    private User testOfficer;
    private PolicySubscription testSub;

    @BeforeEach
    void setUp() {
        Role custRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_CUSTOMER").isActive(true).build()));
        Role officerRole = roleRepository.findByRoleName("ROLE_CLAIMS_OFFICER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_CLAIMS_OFFICER").isActive(true).build()));

        testCustomer = userRepository.save(User.builder().fullName("Claim Cust")
                .email("claimcust@test.com").passwordHash("h").role(custRole).build());
        testOfficer = userRepository.save(User.builder().fullName("Claims Off")
                .email("officer@test.com").passwordHash("h").role(officerRole).build());

        CyberPolicy policy = policyRepository.save(CyberPolicy.builder()
                .policyName("ClaimPol").sector("IT")
                .basePremium(BigDecimal.TEN).coverageLimit(BigDecimal.valueOf(10000)).durationMonths(12).build());
        RiskAssessment risk = riskAssessmentRepository.save(RiskAssessment.builder()
                .customer(testCustomer).riskScore(60).build());
        testSub = subscriptionRepository.save(PolicySubscription.builder()
                .customer(testCustomer).policy(policy).riskAssessment(risk)
                .status(SubscriptionStatus.ACTIVE).build());
    }

    private IncidentReport buildIncident() {
        return incidentReportRepository.save(IncidentReport.builder()
                .customer(testCustomer).subscription(testSub)
                .incidentType("DDoS").status(IncidentStatus.REPORTED)
                .estimatedLossAmount(BigDecimal.valueOf(100))
                .build());
    }

    @Test
    void findByStatus_returnsClaimsWithThatStatus() {
        IncidentReport incident = buildIncident();
        claimRepository.save(Claim.builder()
                .customer(testCustomer).incident(incident)
                .status(ClaimStatus.PENDING).claimAmount(BigDecimal.valueOf(200))
                .filedAt(LocalDateTime.now()).build());

        List<Claim> result = claimRepository.findByStatus(ClaimStatus.PENDING);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getStatus() == ClaimStatus.PENDING));
    }

    @Test
    void findByCustomerOrderByFiledAtDesc_returnsClaimsForCustomer() {
        IncidentReport incident = buildIncident();
        claimRepository.save(Claim.builder()
                .customer(testCustomer).incident(incident)
                .status(ClaimStatus.PENDING).claimAmount(BigDecimal.valueOf(100))
                .filedAt(LocalDateTime.now()).build());

        List<Claim> result = claimRepository.findByCustomerOrderByFiledAtDesc(testCustomer);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getCustomer().getEmail().equals("claimcust@test.com")));
    }

    @Test
    void findByAssignedOfficer_returnsAssignedClaims() {
        IncidentReport incident = buildIncident();
        claimRepository.save(Claim.builder()
                .customer(testCustomer).incident(incident)
                .status(ClaimStatus.UNDER_INVESTIGATION)
                .assignedOfficer(testOfficer)
                .claimAmount(BigDecimal.valueOf(300))
                .filedAt(LocalDateTime.now()).build());

        List<Claim> result = claimRepository.findByAssignedOfficerOrderByFiledAtDesc(testOfficer);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getAssignedOfficer().getEmail().equals("officer@test.com")));
    }

    @Test
    void countByCustomerAndStatus_returnsCorrectCount() {
        IncidentReport incident = buildIncident();
        claimRepository.save(Claim.builder().customer(testCustomer).incident(incident)
                .status(ClaimStatus.APPROVED).claimAmount(BigDecimal.valueOf(100))
                .filedAt(LocalDateTime.now()).build());
        claimRepository.save(Claim.builder().customer(testCustomer).incident(incident)
                .status(ClaimStatus.APPROVED).claimAmount(BigDecimal.valueOf(200))
                .filedAt(LocalDateTime.now()).build());

        long count = claimRepository.countByCustomerAndStatus(testCustomer, ClaimStatus.APPROVED);
        assertEquals(2, count);
    }

    @Test
    void sumClaimAmountByCustomerAndStatusSince_returnsCorrectSum() {
        IncidentReport incident = buildIncident();
        claimRepository.save(Claim.builder().customer(testCustomer).incident(incident)
                .status(ClaimStatus.APPROVED).claimAmount(BigDecimal.valueOf(100))
                .filedAt(LocalDateTime.now()).build());
        claimRepository.save(Claim.builder().customer(testCustomer).incident(incident)
                .status(ClaimStatus.APPROVED).claimAmount(BigDecimal.valueOf(50))
                .filedAt(LocalDateTime.now()).build());

        BigDecimal sum = claimRepository.sumClaimAmountByCustomerAndStatusSince(
                testCustomer, ClaimStatus.APPROVED, LocalDateTime.now().minusDays(1));
        assertEquals(0, BigDecimal.valueOf(150).compareTo(sum));
    }
}
