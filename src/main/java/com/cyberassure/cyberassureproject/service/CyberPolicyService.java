package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreatePolicyRequest;
import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
import com.cyberassure.cyberassureproject.dto.PolicyResponse;
import com.cyberassure.cyberassureproject.entity.CyberPolicy;
import com.cyberassure.cyberassureproject.repository.CyberPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CyberPolicyService {

    private final CyberPolicyRepository repository;

    public PolicyResponse createPolicy(CyberPolicyRequest request) {

        CyberPolicy policy = CyberPolicy.builder()
                .policyName(request.getPolicyName())
                .sector(request.getSector())
                .description(request.getDescription())
                .coverageLimit(request.getCoverageLimit())
                .basePremium(request.getBasePremium())
                .durationMonths(request.getDurationMonths())
                .benefits(request.getBenefits())
                .applicableTo(request.getApplicableTo())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(policy);
        return mapToResponse(policy);
    }

    public PolicyResponse updatePolicy(Long id, CyberPolicyRequest request) {
        CyberPolicy policy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        policy.setPolicyName(request.getPolicyName());
        policy.setSector(request.getSector());
        policy.setDescription(request.getDescription());
        policy.setCoverageLimit(request.getCoverageLimit());
        policy.setBasePremium(request.getBasePremium());
        policy.setDurationMonths(request.getDurationMonths());
        policy.setBenefits(request.getBenefits());
        policy.setApplicableTo(request.getApplicableTo());

        repository.save(policy);
        return mapToResponse(policy);
    }

    public void deletePolicy(Long id) {
        repository.deleteById(id);
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
                .sector(policy.getSector())
                .description(policy.getDescription())
                .benefits(policy.getBenefits())
                .applicableTo(policy.getApplicableTo())
                .isActive(policy.getIsActive())
                .build();
    }
}