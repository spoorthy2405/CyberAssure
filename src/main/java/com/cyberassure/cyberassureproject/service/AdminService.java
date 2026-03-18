package com.cyberassure.cyberassureproject.service;

import com.cyberassure.cyberassureproject.dto.CreateStaffRequest;
import com.cyberassure.cyberassureproject.entity.Role;
import com.cyberassure.cyberassureproject.entity.User;
import com.cyberassure.cyberassureproject.repository.RoleRepository;
import com.cyberassure.cyberassureproject.repository.UserRepository;
import com.cyberassure.cyberassureproject.repository.CyberPolicyRepository;
import com.cyberassure.cyberassureproject.repository.ClaimRepository;
import com.cyberassure.cyberassureproject.entity.CyberPolicy;
import com.cyberassure.cyberassureproject.entity.Claim;
import com.cyberassure.cyberassureproject.dto.CyberPolicyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CyberPolicyRepository policyRepository;
    private final com.cyberassure.cyberassureproject.repository.PolicySubscriptionRepository subscriptionRepository;
    private final ClaimRepository claimRepository;
    private final PasswordEncoder passwordEncoder;

    public User createStaff(CreateStaffRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!request.getRoleName().equals("ROLE_UNDERWRITER")
                && !request.getRoleName().equals("ROLE_CLAIMS_OFFICER")) {
            throw new RuntimeException("Invalid staff role");
        }

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .companyName("CyberAssure Internal")
                .phoneNumber("NA")
                .role(role)
                .accountStatus("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public List<User> getCustomers() {

        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_CUSTOMER"))
                .toList();

    }

    public List<User> getStaff() {
        return userRepository.findAll()
                .stream()
                .filter(u -> !u.getRole().getRoleName().equals("ROLE_CUSTOMER"))
                .toList();
    }

    public User updateStaff(Long userId, com.cyberassure.cyberassureproject.dto.UpdateStaffRequest request) {
        User staff = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        // Don't allow changing role if they aren't staff
        if (staff.getRole().getRoleName().equals("ROLE_CUSTOMER")) {
            throw new RuntimeException("Cannot update customer via staff endpoint");
        }

        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            staff.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Handle Role Update
        if (!staff.getRole().getRoleName().equals(request.getRoleName())) {
            if (!request.getRoleName().equals("ROLE_UNDERWRITER")
                    && !request.getRoleName().equals("ROLE_CLAIMS_OFFICER")) {
                throw new RuntimeException("Invalid staff role");
            }
            Role role = roleRepository.findByRoleName(request.getRoleName())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            staff.setRole(role);
        }

        return userRepository.save(staff);
    }

    public void deactivateStaff(Long userId) {
        User staff = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        if (staff.getRole().getRoleName().equals("ROLE_CUSTOMER")) {
            throw new RuntimeException("Cannot deactivate customer via staff endpoint");
        }

        // Toggle logic based on current status
        if ("ACTIVE".equals(staff.getAccountStatus())) {
             staff.setAccountStatus("INACTIVE");
        } else {
             staff.setAccountStatus("ACTIVE");
        }
        
        userRepository.save(staff);
    }

    public List<User> getUnderwriters() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_UNDERWRITER"))
                .toList();
    }

    public List<User> getClaimsOfficers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().getRoleName().equals("ROLE_CLAIMS_OFFICER"))
                .toList();
    }

    public List<CyberPolicy> getAllCyberPolicies() {
        return policyRepository.findAll();
    }

    public CyberPolicy createCyberPolicy(CyberPolicyRequest request) {
        CyberPolicy policy = CyberPolicy.builder()
                .policyName(request.getPolicyName())
                .sector(request.getSector())
                .description(request.getDescription())
                .coverageLimit(request.getCoverageLimit())
                .basePremium(request.getBasePremium())
                .durationMonths(request.getDurationMonths())
                .benefits(request.getBenefits())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        return policyRepository.save(policy);
    }

    public CyberPolicy updateCyberPolicy(Long id, CyberPolicyRequest request) {
        CyberPolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cyber Policy not found"));

        policy.setPolicyName(request.getPolicyName());
        policy.setSector(request.getSector());
        policy.setDescription(request.getDescription());
        policy.setCoverageLimit(request.getCoverageLimit());
        policy.setBasePremium(request.getBasePremium());
        policy.setDurationMonths(request.getDurationMonths());
        policy.setBenefits(request.getBenefits());

        return policyRepository.save(policy);
    }

    public void deleteCyberPolicy(Long id) {
        policyRepository.deleteById(id);
    }

    public void assignUnderwriter(Long subscriptionId, Long underwriterId) {
        com.cyberassure.cyberassureproject.entity.PolicySubscription subscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        User underwriter = userRepository.findById(underwriterId)
                .orElseThrow(() -> new RuntimeException("Underwriter not found"));

        if (!underwriter.getRole().getRoleName().equals("ROLE_UNDERWRITER")) {
            throw new RuntimeException("Assigned user must be an underwriter");
        }

        subscription.setAssignedUnderwriter(underwriter);
        subscriptionRepository.save(subscription);
    }

    public void assignClaimsOfficer(Long claimId, Long officerId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Claims Officer not found"));

        if (!officer.getRole().getRoleName().equals("ROLE_CLAIMS_OFFICER")) {
            throw new RuntimeException("Assigned user must be a claims officer");
        }

        claim.setAssignedOfficer(officer);
        claimRepository.save(claim);
    }
}