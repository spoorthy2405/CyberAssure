package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreatePolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.entity.CyberPolicy;
import com.cyberassure.cyberassureproject.repository.CyberPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CyberPolicyService {

    private final CyberPolicyRepository repository;

    public PolicyResponse createPolicy(CreatePolicyRequest request) {

        CyberPolicy policy = CyberPolicy.builder()
                .policyName(request.getPolicyName())
                .coverageLimit(request.getCoverageLimit())
                .basePremium(request.getBasePremium())
                .durationMonths(request.getDurationMonths())
                .isActive(true)
                .build();

        repository.save(policy);

        return mapToResponse(policy);
    }

    public List<PolicyResponse> getAllPolicies() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PolicyResponse mapToResponse(CyberPolicy policy) {

        return PolicyResponse.builder()
                .id(policy.getId())
                .policyName(policy.getPolicyName())
                .coverageLimit(policy.getCoverageLimit())
                .basePremium(policy.getBasePremium())
                .durationMonths(policy.getDurationMonths())
                .isActive(policy.getIsActive())
                .build();
    }
}