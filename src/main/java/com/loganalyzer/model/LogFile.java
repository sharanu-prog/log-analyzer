package com.loganalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column
    private int totalLines;

    @Column
    private int errorCount;

    @Column
    private int warnCount;

    @Column
    private int fatalCount;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}