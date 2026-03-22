package com.loganalyzer.service;

import com.loganalyzer.model.FailurePattern;
import com.loganalyzer.model.LogEvent;
import com.loganalyzer.repository.FailurePatternRepository;
import com.loganalyzer.repository.LogEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatternDetectionService {

    private final LogEventRepository logEventRepository;
    private final FailurePatternRepository failurePatternRepository;

    // 🧠 Main method — analyze all events and detect patterns
    public List<FailurePattern> detectPatterns() {

        // 1. Get all ERROR and FATAL events
        List<LogEvent> errorEvents = logEventRepository.findAll()
                .stream()
                .filter(e -> e.getLevel().equals("ERROR") || e.getLevel().equals("FATAL"))
                .toList();

        // 2. Group events by normalized message
        Map<String, List<LogEvent>> grouped = new HashMap<>();

        for (LogEvent event : errorEvents) {
            String normalized = normalizeMessage(event.getMessage());
            grouped.computeIfAbsent(normalized, k -> new ArrayList<>()).add(event);
        }

        // 3. Save patterns to DB
        List<FailurePattern> patterns = new ArrayList<>();

        for (Map.Entry<String, List<LogEvent>> entry : grouped.entrySet()) {
            String message = entry.getKey();
            List<LogEvent> events = entry.getValue();

            // Only consider it a pattern if it appears more than once
            // (or at least once for FATAL)
            String level = events.get(0).getLevel();
            int count = events.size();

            // Check if pattern already exists in DB
            Optional<FailurePattern> existing =
                    failurePatternRepository.findByPatternMessageAndLevel(message, level);

            FailurePattern pattern;
            if (existing.isPresent()) {
                // Update existing pattern
                pattern = existing.get();
                pattern.setOccurrenceCount(count);
                pattern.setLastSeen(LocalDateTime.now());
            } else {
                // Create new pattern
                pattern = new FailurePattern();
                pattern.setPatternMessage(message);
                pattern.setLevel(level);
                pattern.setOccurrenceCount(count);
                pattern.setFirstSeen(LocalDateTime.now());
                pattern.setLastSeen(LocalDateTime.now());
            }

            // Calculate severity score
            // FATAL = 10 points, ERROR = 5 points, multiplied by frequency
            double baseScore = level.equals("FATAL") ? 10.0 : 5.0;
            pattern.setSeverityScore(baseScore * count);

            patterns.add(failurePatternRepository.save(pattern));
        }

        // 4. Return sorted by severity
        patterns.sort((a, b) ->
                Double.compare(b.getSeverityScore(), a.getSeverityScore()));

        return patterns;
    }

    // 📋 Get all saved patterns
    public List<FailurePattern> getAllPatterns() {
        return failurePatternRepository.findAllByOrderByOccurrenceCountDesc();
    }

    // 📋 Get patterns by level
    public List<FailurePattern> getPatternsByLevel(String level) {
        return failurePatternRepository.findByLevelOrderByOccurrenceCountDesc(level);
    }

    // 🔧 Normalize a log message to find duplicates
    // Removes timestamps, IDs, line numbers so similar errors are grouped
    private String normalizeMessage(String message) {
        if (message == null || message.isEmpty()) return "";

        String normalized = message
                .replaceAll("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}", "")
                .replaceAll("\\b\\d+\\b", "#")
                .replaceAll("\\s+", " ")
                .trim();

        // ✅ Fix: use normalized length, not original message length
        return normalized.substring(0, Math.min(200, normalized.length()));
    }
}