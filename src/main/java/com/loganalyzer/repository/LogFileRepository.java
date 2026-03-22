package com.loganalyzer.repository;

import com.loganalyzer.model.LogFile;
import com.loganalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogFileRepository extends JpaRepository<LogFile, Long> {
    List<LogFile> findByUploadedByOrderByUploadedAtDesc(User user);
    List<LogFile> findAllByOrderByUploadedAtDesc();
}