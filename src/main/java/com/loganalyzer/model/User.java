package com.loganalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// 🗄️ @Entity = this class maps to a database table
@Entity
@Table(name = "users")
@Data               // Lombok: auto-generates getters, setters, toString
@NoArgsConstructor  // Lombok: generates empty constructor
@AllArgsConstructor // Lombok: generates constructor with all fields
public class User {

    // 🔑 Primary key, auto increments (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 Username must be unique and not null
    @Column(nullable = false, unique = true)
    private String username;

    // 📧 Email must be unique
    @Column(nullable = false, unique = true)
    private String email;

    // 🔒 Password (will be stored encrypted, never plain text!)
    @Column(nullable = false)
    private String password;

    // 👮 Role: either "ROLE_USER" or "ROLE_ADMIN"
    @Column(nullable = false)
    private String role = "ROLE_USER";
}