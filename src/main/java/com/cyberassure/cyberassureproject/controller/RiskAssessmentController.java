package com.cyberassure.cyberassureproject.controller;

import com.cyberassure.cyberassureproject.dto.CreateRiskAssessmentRequest;
import com.cyberassure.cyberassureproject.entity.RiskAssessment;
import com.cyberassure.cyberassureproject.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/risk")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService service;

    @PostMapping
    public ResponseEntity<RiskAssessment> submitRisk(
            @RequestBody CreateRiskAssessmentRequest request) {

        return ResponseEntity.ok(service.submitRisk(request));
    }

    @GetMapping
    public ResponseEntity<List<RiskAssessment>> getAllRiskAssessments() {
        return ResponseEntity.ok(service.getAll());
    }

}