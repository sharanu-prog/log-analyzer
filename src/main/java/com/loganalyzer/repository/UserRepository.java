package com.loganalyzer.repository;

import com.loganalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 🗄️ JpaRepository gives us save(), findAll(), findById(), delete() for FREE
// No need to write SQL queries for basic operations!
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring auto-generates the SQL for these just from the method name! 🪄
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}