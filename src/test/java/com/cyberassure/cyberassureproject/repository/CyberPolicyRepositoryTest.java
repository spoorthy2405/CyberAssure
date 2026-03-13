package com.cyberassure.cyberassureproject.repository;

import com.cyberassure.cyberassureproject.entity.CyberPolicy;
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
class CyberPolicyRepositoryTest {

    @Autowired
    CyberPolicyRepository cyberPolicyRepository;

    /**
     * Uses unique sector names "UNIQUE_FINTECH" and "UNIQUE_BIOTECH" that the seeder
     * will never create, so we can assert exact counts without interference.
     */
    @Test
    void findBySector_returnsMatchingPolicies() {
        cyberPolicyRepository.save(CyberPolicy.builder()
                .policyName("Fintech Shield").sector("UNIQUE_FINTECH")
                .basePremium(BigDecimal.valueOf(1000))
                .coverageLimit(BigDecimal.valueOf(50000))
                .durationMonths(12)
                .build());

        cyberPolicyRepository.save(CyberPolicy.builder()
                .policyName("Biotech Protect").sector("UNIQUE_BIOTECH")
                .basePremium(BigDecimal.valueOf(2000))
                .coverageLimit(BigDecimal.valueOf(100000))
                .durationMonths(12)
                .build());

        List<CyberPolicy> policies = cyberPolicyRepository
                .findBySectorIgnoreCaseOrSectorIgnoreCase("unique_fintech", "unique_biotech");

        assertEquals(2, policies.size());
        assertTrue(policies.stream().anyMatch(p -> "Fintech Shield".equals(p.getPolicyName())));
        assertTrue(policies.stream().anyMatch(p -> "Biotech Protect".equals(p.getPolicyName())));
    }

    @Test
    void findBySector_returnsEmpty_whenNoMatch() {
        List<CyberPolicy> policies = cyberPolicyRepository
                .findBySectorIgnoreCaseOrSectorIgnoreCase(
                        "COMPLETELY_NONEXISTENT_SECTOR_XYZ", "ANOTHER_NONEXISTENT_SECTOR_ABC");
        assertTrue(policies.isEmpty());
    }
}
