package com.cyberassure.cyberassureproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStaffRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    // Optional: Only provided if the admin wants to reset their password
    private String password;

    @NotBlank
    private String roleName; 
}
