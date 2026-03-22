package com.loganalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "failure_patterns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailurePattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The recurring error message/pattern
    @Column(columnDefinition = "TEXT")
    private String patternMessage;

    // How many times this pattern appeared
    @Column(nullable = false)
    private int occurrenceCount;

    // ERROR, WARN, FATAL
    @Column(nullable = false)
    private String level;

    // First time this pattern was seen
    @Column
    private LocalDateTime firstSeen;

    // Last time this pattern was seen
    @Column
    private LocalDateTime lastSeen;

    // Severity score (calculated based on level + frequency)
    @Column
    private double severityScore;
}