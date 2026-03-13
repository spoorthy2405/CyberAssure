package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.entity.CyberPolicy;
import com.cyberassure.cyberassureproject.exception.ResourceNotFoundException;
import com.cyberassure.cyberassureproject.repository.CyberPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CyberPolicyServiceTest {

    @Mock private CyberPolicyRepository repository;
    @InjectMocks private CyberPolicyService service;

    private CyberPolicy samplePolicy;
    private CyberPolicyRequest sampleRequest;

    @BeforeEach
    void setUp() {
        samplePolicy = CyberPolicy.builder()
                .id(1L).policyName("Tech Shield").sector("IT")
                .basePremium(BigDecimal.valueOf(500)).coverageLimit(BigDecimal.valueOf(10000))
                .durationMonths(12).isActive(true).build();

        sampleRequest = new CyberPolicyRequest();
        sampleRequest.setPolicyName("Tech Shield");
        sampleRequest.setSector("IT");
        sampleRequest.setBasePremium(BigDecimal.valueOf(500));
        sampleRequest.setCoverageLimit(BigDecimal.valueOf(10000));
        sampleRequest.setDurationMonths(12);
    }

    @Test
    void createPolicy_ReturnsCreatedPolicy() {
        when(repository.save(any(CyberPolicy.class))).thenReturn(samplePolicy);
        PolicyResponse result = service.createPolicy(sampleRequest);

        assertNotNull(result);
        assertEquals("Tech Shield", result.getPolicyName());
        assertEquals("IT", result.getSector());
        verify(repository, times(1)).save(any(CyberPolicy.class));
    }

    @Test
    void updatePolicy_Success_WhenPolicyExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(samplePolicy));
        when(repository.save(any(CyberPolicy.class))).thenReturn(samplePolicy);

        sampleRequest.setPolicyName("Updated Shield");
        PolicyResponse result = service.updatePolicy(1L, sampleRequest);

        assertNotNull(result);
        verify(repository).save(any(CyberPolicy.class));
    }

    @Test
    void updatePolicy_ThrowsResourceNotFoundException_WhenPolicyMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updatePolicy(99L, sampleRequest));
    }

    @Test
    void deletePolicy_CallsDeleteById() {
        doNothing().when(repository).deleteById(1L);
        service.deletePolicy(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void getAllPolicies_ReturnsListOfPolicies() {
        when(repository.findAll()).thenReturn(List.of(samplePolicy));
        List<PolicyResponse> results = service.getAllPolicies();

        assertEquals(1, results.size());
        assertEquals("Tech Shield", results.get(0).getPolicyName());
    }

    @Test
    void getAllPolicies_ReturnsEmptyList_WhenNoPoliciesExist() {
        when(repository.findAll()).thenReturn(List.of());
        List<PolicyResponse> results = service.getAllPolicies();
        assertTrue(results.isEmpty());
    }
}
