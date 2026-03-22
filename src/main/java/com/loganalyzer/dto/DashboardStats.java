package com.loganalyzer.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

// This is what we send back to the frontend dashboard
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {

    // Total counts
    private long totalLogFiles;
    private long totalEvents;
    private long totalErrors;
    private long totalWarnings;
    private long totalFatals;
    private long totalPatterns;

    // Top 5 most recurring errors
    private List<Map<String, Object>> topErrors;

    // Recent log files (last 5)
    private List<Map<String, Object>> recentLogFiles;
}