package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.ClaimAssessmentResult;
import com.cyberassure.cyberassureproject.dto.ClaimDecisionRequest;
import com.cyberassure.cyberassureproject.dto.CreateClaimRequest;
import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.entity.ClaimStatus;
import com.cyberassure.cyberassureproject.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    private Principal mockPrincipal;
    private Claim pendingClaim;

    @BeforeEach
    void setUp() {
        mockPrincipal = mock(Principal.class);
        lenient().when(mockPrincipal.getName()).thenReturn("test@test.com");
        pendingClaim = Claim.builder().id(1L).status(ClaimStatus.PENDING).build();
    }

    @Test
    void fileClaim_ReturnsOk() {
        CreateClaimRequest req = new CreateClaimRequest();
        when(claimService.fileClaim(any(CreateClaimRequest.class))).thenReturn(pendingClaim);

        ResponseEntity<Claim> response = claimController.fileClaim(req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getClaimAssessment_ReturnsResult() {
        ClaimAssessmentResult result = new ClaimAssessmentResult();
        result.setRecommendedPayout(BigDecimal.valueOf(100));
        when(claimService.assessClaim(1L)).thenReturn(result);

        ResponseEntity<ClaimAssessmentResult> response = claimController.getClaimAssessment(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(BigDecimal.valueOf(100), response.getBody().getRecommendedPayout());
    }

    @Test
    void getMyClaims_ReturnsList() {
        when(claimService.getClaimsByCustomer("test@test.com")).thenReturn(List.of(pendingClaim));
        ResponseEntity<List<Claim>> response = claimController.getMyClaims(mockPrincipal);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void reviewClaim_ReturnsOk() {
        ClaimDecisionRequest req = new ClaimDecisionRequest();
        Claim approved = Claim.builder().id(1L).status(ClaimStatus.APPROVED).build();
        when(claimService.reviewClaim(eq(1L), any())).thenReturn(approved);

        ResponseEntity<Claim> response = claimController.reviewClaim(1L, req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(ClaimStatus.APPROVED, response.getBody().getStatus());
    }

    @Test
    void settleClaim_ReturnsOk() {
        Map<String, Object> req = Map.of("settlementAmount", 500, "notes", "Settled");
        Claim settled = Claim.builder().id(1L).status(ClaimStatus.SETTLED).settlementAmount(BigDecimal.valueOf(500)).build();
        when(claimService.settleClaim(eq(1L), any(BigDecimal.class), anyString())).thenReturn(settled);

        ResponseEntity<Claim> response = claimController.settleClaim(1L, req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(ClaimStatus.SETTLED, response.getBody().getStatus());
    }

    @Test
    void getAllClaims_ReturnsList() {
        when(claimService.getAllClaims()).thenReturn(List.of(pendingClaim));
        ResponseEntity<List<Claim>> response = claimController.getAllClaims();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
