package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateSubscriptionRequest;
import com.cyberassure.cyberassureproject.dto.UnderwriterDecisionRequest;
import com.cyberassure.cyberassureproject.entity.PolicySubscription;
import com.cyberassure.cyberassureproject.service.PolicySubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PolicySubscriptionControllerTest {

    @Mock
    private PolicySubscriptionService service;

    @InjectMocks
    private PolicySubscriptionController controller;

    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        mockPrincipal = mock(Principal.class);
        lenient().when(mockPrincipal.getName()).thenReturn("test@test.com");
    }

    @Test
    void subscribe_ReturnsOk() {
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        PolicySubscription resp = PolicySubscription.builder().id(1L).build();
        when(service.subscribe(any(CreateSubscriptionRequest.class))).thenReturn(resp);

        ResponseEntity<PolicySubscription> response = controller.subscribe(req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getMySubscriptions_ReturnsList() {
        PolicySubscription sub = PolicySubscription.builder().id(1L).build();
        when(service.getSubscriptionsByCustomer("test@test.com")).thenReturn(List.of(sub));

        ResponseEntity<List<PolicySubscription>> response = controller.getMySubscriptions(mockPrincipal);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void review_ReturnsOk() {
        UnderwriterDecisionRequest req = new UnderwriterDecisionRequest();
        PolicySubscription resp = PolicySubscription.builder().id(1L).build();
        when(service.reviewSubscription(eq(1L), any())).thenReturn(resp);

        ResponseEntity<PolicySubscription> response = controller.review(1L, req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAllSubscriptions_ReturnsList() {
        PolicySubscription sub = PolicySubscription.builder().id(1L).build();
        when(service.getAllSubscriptions()).thenReturn(List.of(sub));

        ResponseEntity<List<PolicySubscription>> response = controller.getAllSubscriptions();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getMyAssigned_ReturnsList() {
        PolicySubscription sub = PolicySubscription.builder().id(1L).build();
        when(service.getAssignedToMe("test@test.com")).thenReturn(List.of(sub));

        ResponseEntity<List<PolicySubscription>> response = controller.getMyAssigned(mockPrincipal);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void paySubscription_ReturnsOk() {
        PolicySubscription resp = PolicySubscription.builder().id(1L).build();
        when(service.paySubscription(1L)).thenReturn(resp);

        ResponseEntity<PolicySubscription> response = controller.paySubscription(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }
}
