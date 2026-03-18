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
class IncidentReportRepositoryTest {

    @Autowired
    IncidentReportRepository incidentReportRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private User testCustomer;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().roleName("ROLE_CUSTOMER").isActive(true).build()));
        testCustomer = userRepository.save(User.builder().fullName("Inc Cust")
                .email("inccust@test.com").passwordHash("h").role(role).build());
    }

    @Test
    void findByCustomerOrderByReportedAtDesc_returnsOrderedIncidents() {
        incidentReportRepository.save(IncidentReport.builder()
                .customer(testCustomer).incidentType("Phishing")
                .status(IncidentStatus.REPORTED)
                .reportedAt(LocalDateTime.now().minusDays(3))
                .estimatedLossAmount(BigDecimal.valueOf(500))
                .build());

        incidentReportRepository.save(IncidentReport.builder()
                .customer(testCustomer).incidentType("Ransomware")
                .status(IncidentStatus.REPORTED)
                .reportedAt(LocalDateTime.now().minusDays(1))
                .estimatedLossAmount(BigDecimal.valueOf(9000))
                .build());

        List<IncidentReport> results = incidentReportRepository
                .findByCustomerOrderByReportedAtDesc(testCustomer);

        assertEquals(2, results.size());
        assertEquals("Ransomware", results.get(0).getIncidentType());
        assertEquals("Phishing", results.get(1).getIncidentType());
    }

    @Test
    void findByCustomerOrderByReportedAtDesc_returnsEmpty_whenNoIncidents() {
        List<IncidentReport> results = incidentReportRepository
                .findByCustomerOrderByReportedAtDesc(testCustomer);
        assertTrue(results.isEmpty());
    }
}
