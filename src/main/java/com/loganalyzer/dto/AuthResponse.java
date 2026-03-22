package com.loganalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// This is what we SEND BACK to frontend after login
// Contains the JWT token
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}