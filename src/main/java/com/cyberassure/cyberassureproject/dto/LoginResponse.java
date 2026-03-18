package com.cyberassure.cyberassureproject.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String message;
    private String email;
    private String role;
    private String token;

}
