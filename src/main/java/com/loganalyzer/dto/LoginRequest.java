package com.loganalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// This is what the frontend SENDS when logging in
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}