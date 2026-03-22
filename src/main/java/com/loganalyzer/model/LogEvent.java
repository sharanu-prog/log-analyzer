package com.loganalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which log file this event belongs to
    @ManyToOne
    @JoinColumn(name = "log_file_id")
    private LogFile logFile;

    // Line number in the original file
    @Column
    private int lineNumber;

    // ERROR, WARN, FATAL, INFO
    @Column(nullable = false)
    private String level;

    // The actual log message
    @Column(columnDefinition = "TEXT")
    private String message;

    // Timestamp parsed from the log line (if available)
    @Column
    private String timestamp;

    // When we saved this event
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}