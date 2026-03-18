package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateRiskAssessmentRequest;
import com.cyberassure.cyberassureproject.entity.RiskAssessment;
import com.cyberassure.cyberassureproject.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentControllerTest {

    @Mock
    private RiskAssessmentService service;

    @InjectMocks
    private RiskAssessmentController controller;

    @Test
    void submitRisk_ReturnsOk() {
        CreateRiskAssessmentRequest req = new CreateRiskAssessmentRequest();
        RiskAssessment resp = RiskAssessment.builder().id(1L).riskScore(100).build();
        when(service.submitRisk(any(CreateRiskAssessmentRequest.class))).thenReturn(resp);

        ResponseEntity<RiskAssessment> response = controller.submitRisk(req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(100, response.getBody().getRiskScore());
    }

    @Test
    void getAllRiskAssessments_ReturnsList() {
        RiskAssessment resp = RiskAssessment.builder().id(1L).build();
        when(service.getAll()).thenReturn(List.of(resp));

        ResponseEntity<List<RiskAssessment>> response = controller.getAllRiskAssessments();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
