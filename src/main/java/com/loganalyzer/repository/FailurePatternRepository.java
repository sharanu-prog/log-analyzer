package com.loganalyzer.repository;

import com.loganalyzer.model.FailurePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FailurePatternRepository extends JpaRepository<FailurePattern, Long> {

    // Find pattern by message and level
    Optional<FailurePattern> findByPatternMessageAndLevel(String patternMessage, String level);

    // Get all patterns sorted by occurrence (most frequent first)
    List<FailurePattern> findAllByOrderByOccurrenceCountDesc();

    // Get top patterns by level
    List<FailurePattern> findByLevelOrderByOccurrenceCountDesc(String level);
}