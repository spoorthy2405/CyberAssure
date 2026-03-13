package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateRiskAssessmentRequest;
import com.cyberassure.cyberassureproject.entity.RiskAssessment;
import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.RiskAssessmentRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceTest {

    @Mock private RiskAssessmentRepository repository;
    @Mock private UserRepository userRepository;
    @InjectMocks private RiskAssessmentService service;

    private User customer;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().roleName("ROLE_CUSTOMER").roleId(1L).isActive(true).build();
        customer = User.builder().userId(1L).email("cust@test.com").fullName("Customer").role(role).build();
    }

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private CreateRiskAssessmentRequest fullRequest(boolean fw, boolean enc, boolean bkp, boolean mfa, int prevInc) {
        CreateRiskAssessmentRequest r = new CreateRiskAssessmentRequest();
        r.setFirewallEnabled(fw);
        r.setEncryptionEnabled(enc);
        r.setBackupAvailable(bkp);
        r.setMfaEnabled(mfa);
        r.setPreviousIncidentCount(prevInc);
        r.setEmployeeCount(50);
        return r;
    }

    @Test
    void submitRisk_AllSecure_LowRiskScore() {
        mockSecurityContext("cust@test.com");
        CreateRiskAssessmentRequest req = fullRequest(true, true, true, true, 0);

        RiskAssessment saved = RiskAssessment.builder().riskScore(100).riskLevel("LOW").customer(customer).build();
        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(repository.save(any())).thenReturn(saved);

        RiskAssessment result = service.submitRisk(req);
        assertEquals("LOW", result.getRiskLevel());
        verify(repository, times(1)).save(any());
    }

    @Test
    void submitRisk_AllVulnerable_HighRiskScore() {
        mockSecurityContext("cust@test.com");
        CreateRiskAssessmentRequest req = fullRequest(false, false, false, false, 7);

        RiskAssessment saved = RiskAssessment.builder().riskScore(220).riskLevel("HIGH").customer(customer).build();
        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(customer));
        when(repository.save(any())).thenReturn(saved);

        RiskAssessment result = service.submitRisk(req);
        assertEquals("HIGH", result.getRiskLevel());
    }

    @Test
    void submitRisk_ThrowsResourceNotFoundException_WhenUserNotFound() {
        mockSecurityContext("ghost@test.com");
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.submitRisk(fullRequest(true, true, true, true, 0)));
    }

    @Test
    void getAll_ReturnsList() {
        RiskAssessment ra = RiskAssessment.builder().riskScore(100).riskLevel("LOW").customer(customer).build();
        when(repository.findAll()).thenReturn(List.of(ra));
        List<RiskAssessment> result = service.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getAll_ReturnsEmptyList_WhenNoAssessments() {
        when(repository.findAll()).thenReturn(List.of());
        List<RiskAssessment> result = service.getAll();
        assertTrue(result.isEmpty());
    }
}
