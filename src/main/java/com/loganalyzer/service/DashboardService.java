package com.loganalyzer.service;

import com.loganalyzer.dto.DashboardStats;
import com.loganalyzer.model.LogFile;
import com.loganalyzer.repository.FailurePatternRepository;
import com.loganalyzer.repository.LogEventRepository;
import com.loganalyzer.repository.LogFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LogFileRepository logFileRepository;
    private final LogEventRepository logEventRepository;
    private final FailurePatternRepository failurePatternRepository;

    public DashboardStats getDashboardStats() {

        DashboardStats stats = new DashboardStats();

        // 1. Total counts
        stats.setTotalLogFiles(logFileRepository.count());
        stats.setTotalEvents(logEventRepository.count());
        stats.setTotalErrors(logEventRepository.countByLevel("ERROR"));
        stats.setTotalWarnings(logEventRepository.countByLevel("WARN"));
        stats.setTotalFatals(logEventRepository.countByLevel("FATAL"));
        stats.setTotalPatterns(failurePatternRepository.count());

        // 2. Top 5 recurring errors
        List<Object[]> topErrorsRaw = logEventRepository.findTopErrorMessages();
        List<Map<String, Object>> topErrors = new ArrayList<>();
        int limit = Math.min(5, topErrorsRaw.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = topErrorsRaw.get(i);
            Map<String, Object> error = new HashMap<>();
            error.put("message", row[0]);
            error.put("count", row[1]);
            topErrors.add(error);
        }
        stats.setTopErrors(topErrors);

        // 3. Recent 5 log files
        List<LogFile> recentFiles = logFileRepository
                .findAllByOrderByUploadedAtDesc()
                .stream()
                .limit(5)
                .toList();

        List<Map<String, Object>> recentLogFiles = new ArrayList<>();
        for (LogFile file : recentFiles) {
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("id", file.getId());
            fileMap.put("fileName", file.getFileName());
            fileMap.put("uploadedAt", file.getUploadedAt());
            fileMap.put("errorCount", file.getErrorCount());
            fileMap.put("warnCount", file.getWarnCount());
            fileMap.put("fatalCount", file.getFatalCount());
            fileMap.put("totalLines", file.getTotalLines());
            recentLogFiles.add(fileMap);
        }
        stats.setRecentLogFiles(recentLogFiles);

        return stats;
    }
}