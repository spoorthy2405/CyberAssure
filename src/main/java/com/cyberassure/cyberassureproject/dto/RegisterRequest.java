package com.cyberassure.cyberassureproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String companyName;

    private String industry;

    private String companySize;

    // New Corporate Identity Fields
    private String companyAddress;
    private String companyWebsite;
    private String registrationNumber;
    private String annualRevenue;

    private String phoneNumber;
}