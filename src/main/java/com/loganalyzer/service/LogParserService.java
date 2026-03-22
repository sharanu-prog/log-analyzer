package com.loganalyzer.service;

import com.loganalyzer.model.LogEvent;
import com.loganalyzer.model.LogFile;
import com.loganalyzer.model.User;
import com.loganalyzer.repository.LogEventRepository;
import com.loganalyzer.repository.LogFileRepository;
import com.loganalyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogParserService {

    private final LogFileRepository logFileRepository;
    private final LogEventRepository logEventRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 📁 Upload and parse a log file
    public LogFile uploadAndParse(MultipartFile file, String username) throws IOException {

        // 1. Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Save file to disk
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 3. Create LogFile record
        LogFile logFile = new LogFile();
        logFile.setFileName(file.getOriginalFilename());
        logFile.setFilePath(filePath.toString());
        logFile.setFileSize(file.getSize());
        logFile.setUploadedBy(user);

        // 4. Parse the file line by line
        List<LogEvent> events = new ArrayList<>();
        int totalLines = 0, errorCount = 0, warnCount = 0, fatalCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile())))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                totalLines++;
                String upperLine = line.toUpperCase();

                // Detect log level
                String level = null;
                if (upperLine.contains("FATAL")) {
                    level = "FATAL";
                    fatalCount++;
                } else if (upperLine.contains("ERROR")) {
                    level = "ERROR";
                    errorCount++;
                } else if (upperLine.contains("WARN")) {
                    level = "WARN";
                    warnCount++;
                }

                // Only save ERROR, WARN, FATAL lines
                if (level != null) {
                    LogEvent event = new LogEvent();
                    event.setLogFile(logFile);
                    event.setLineNumber(lineNumber);
                    event.setLevel(level);
                    event.setMessage(line.length() > 500 ? line.substring(0, 500) : line);
                    event.setTimestamp(extractTimestamp(line));
                    events.add(event);
                }
            }
        }

        // 5. Update counts and save
        logFile.setTotalLines(totalLines);
        logFile.setErrorCount(errorCount);
        logFile.setWarnCount(warnCount);
        logFile.setFatalCount(fatalCount);

        logFileRepository.save(logFile);
        logEventRepository.saveAll(events);

        return logFile;
    }

    // 🕐 Try to extract timestamp from log line
    private String extractTimestamp(String line) {
        // Matches patterns like: 2024-01-15 or 2024-01-15T10:30:00
        if (line.length() >= 10) {
            String start = line.substring(0, 10);
            if (start.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return line.substring(0, Math.min(23, line.length()));
            }
        }
        return null;
    }

    // 📋 Get all log files
    public List<LogFile> getAllLogFiles() {
        return logFileRepository.findAllByOrderByUploadedAtDesc();
    }

    // 📋 Get events for a specific log file
    public List<LogEvent> getEventsForLogFile(Long logFileId) {
        LogFile logFile = logFileRepository.findById(logFileId)
                .orElseThrow(() -> new RuntimeException("Log file not found"));
        return logEventRepository.findByLogFile(logFile);
    }
}