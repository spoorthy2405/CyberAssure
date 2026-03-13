package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.service.CyberPolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CyberPolicyControllerTest {

    @Mock
    private CyberPolicyService service;

    @InjectMocks
    private CyberPolicyController controller;

    @Test
    void createPolicy_ReturnsOk() {
        CyberPolicyRequest req = new CyberPolicyRequest();
        PolicyResponse resp = PolicyResponse.builder().id(1L).policyName("Test").build();
        when(service.createPolicy(any(CyberPolicyRequest.class))).thenReturn(resp);

        ResponseEntity<PolicyResponse> response = controller.createPolicy(req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updatePolicy_ReturnsOk() {
        CyberPolicyRequest req = new CyberPolicyRequest();
        PolicyResponse resp = PolicyResponse.builder().id(1L).policyName("Updated").build();
        when(service.updatePolicy(eq(1L), any(CyberPolicyRequest.class))).thenReturn(resp);

        ResponseEntity<PolicyResponse> response = controller.updatePolicy(1L, req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", response.getBody().getPolicyName());
    }

    @Test
    void deletePolicy_ReturnsNoContent() {
        doNothing().when(service).deletePolicy(1L);
        ResponseEntity<Void> response = controller.deletePolicy(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void getAllPolicies_ReturnsList() {
        PolicyResponse resp = PolicyResponse.builder().id(1L).build();
        when(service.getAllPolicies()).thenReturn(List.of(resp));

        ResponseEntity<List<PolicyResponse>> response = controller.getAllPolicies();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
