package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PolicySubscriptionRepositoryTest {

    @Autowired
    PolicySubscriptionRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CyberPolicyRepository policyRepository;

    @Autowired
    RiskAssessmentRepository riskAssessmentRepository;

    private User testCustomer;
    private User testUnderwriter;
    private CyberPolicy testPolicy;
    private RiskAssessment testRisk;

    @BeforeEach
    void setUp() {
        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_CUSTOMER").isActive(true).build()));
        Role uwRole = roleRepository.findByRoleName("ROLE_UNDERWRITER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_UNDERWRITER").isActive(true).build()));

        testCustomer = userRepository.save(User.builder().fullName("Cust Sub")
                .email("custsub@test.com").passwordHash("h").role(customerRole).build());
        testUnderwriter = userRepository.save(User.builder().fullName("UW Sub")
                .email("uwsub@test.com").passwordHash("h").role(uwRole).build());

        testPolicy = policyRepository.save(CyberPolicy.builder()
                .policyName("SubPolicy").sector("IT")
                .basePremium(BigDecimal.TEN).coverageLimit(BigDecimal.TEN).durationMonths(12).build());

        testRisk = riskAssessmentRepository.save(RiskAssessment.builder()
                .customer(testCustomer).riskScore(55).build());
    }

    @Test
    void findByCustomerAndStatus_returnsActiveSubscription() {
        repository.save(PolicySubscription.builder()
                .customer(testCustomer).policy(testPolicy).riskAssessment(testRisk)
                .status(SubscriptionStatus.ACTIVE).build());

        List<PolicySubscription> results = repository.findByCustomerAndStatus(testCustomer, SubscriptionStatus.ACTIVE);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(s -> s.getStatus() == SubscriptionStatus.ACTIVE));
    }

    @Test
    void findByCustomerAndStatus_returnsEmpty_whenStatusMismatch() {
        repository.save(PolicySubscription.builder()
                .customer(testCustomer).policy(testPolicy).riskAssessment(testRisk)
                .status(SubscriptionStatus.PENDING).build());

        List<PolicySubscription> results = repository.findByCustomerAndStatus(testCustomer, SubscriptionStatus.ACTIVE);
        assertTrue(results.isEmpty());
    }

    @Test
    void findByAssignedUnderwriter_returnsSubscriptionsForUW() {
        repository.save(PolicySubscription.builder()
                .customer(testCustomer).policy(testPolicy).riskAssessment(testRisk)
                .status(SubscriptionStatus.PENDING)
                .assignedUnderwriter(testUnderwriter).build());

        List<PolicySubscription> results = repository.findByAssignedUnderwriter(testUnderwriter);
        assertFalse(results.isEmpty());
        assertEquals(testUnderwriter.getEmail(), results.get(0).getAssignedUnderwriter().getEmail());
    }

    @Test
    void findByCustomer_returnsAllCustomerSubscriptions() {
        repository.save(PolicySubscription.builder().customer(testCustomer).policy(testPolicy)
                .riskAssessment(testRisk).status(SubscriptionStatus.ACTIVE).build());
        repository.save(PolicySubscription.builder().customer(testCustomer).policy(testPolicy)
                .riskAssessment(testRisk).status(SubscriptionStatus.REJECTED).build());

        List<PolicySubscription> results = repository.findByCustomer(testCustomer);
        // seeder may have added some; we just check our 2 are present
        assertTrue(results.size() >= 2);
        long ourSubs = results.stream().filter(s -> s.getCustomer().getEmail().equals("custsub@test.com")).count();
        assertEquals(2, ourSubs);
    }
}
