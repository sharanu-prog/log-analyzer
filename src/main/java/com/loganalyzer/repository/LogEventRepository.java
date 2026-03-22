package com.loganalyzer.repository;

import com.loganalyzer.model.LogEvent;
import com.loganalyzer.model.LogFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {

    List<LogEvent> findByLogFile(LogFile logFile);

    List<LogEvent> findByLogFileAndLevel(LogFile logFile, String level);

    // Count events by level across all files
    long countByLevel(String level);

    // Get top recurring error messages
    @Query("SELECT e.message, COUNT(e) as cnt FROM LogEvent e " +
            "WHERE e.level = 'ERROR' " +
            "GROUP BY e.message ORDER BY cnt DESC")
    List<Object[]> findTopErrorMessages();
}