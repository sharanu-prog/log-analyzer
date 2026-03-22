package com.loganalyzer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// 🔑 This class handles everything related to JWT tokens
@Component
public class JwtUtil {

    // Reads the secret key from application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Reads expiration time from application.properties
    @Value("${app.jwt.expiration}")
    private int jwtExpiration;

    // 🔐 Get signing key from secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ✅ Generate a JWT token for a user
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)           // store username in token
                .setIssuedAt(new Date())        // when token was created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign it
                .compact();
    }

    // 📖 Extract username from token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Check if token is valid
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}