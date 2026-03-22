package com.loganalyzer.service;

import com.loganalyzer.dto.AuthResponse;
import com.loganalyzer.dto.LoginRequest;
import com.loganalyzer.dto.RegisterRequest;
import com.loganalyzer.model.User;
import com.loganalyzer.repository.UserRepository;
import com.loganalyzer.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // 📝 Register a new user
    public String register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Create new user and encrypt the password before saving
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔒 never save plain text!
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "User registered successfully!";
    }

    // 🔐 Login and return JWT token
    public AuthResponse login(LoginRequest request) {
        // This verifies username + password (throws exception if wrong)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If we reach here, credentials are correct — generate token
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}