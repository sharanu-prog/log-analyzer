package com.loganalyzer.controller;

import com.loganalyzer.model.LogEvent;
import com.loganalyzer.model.LogFile;
import com.loganalyzer.service.LogParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class LogController {

    private final LogParserService logParserService;

    @Operation(summary = "Upload a log file")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LogFile> uploadLog(
            @RequestPart("file") MultipartFile file,
            Principal principal) throws IOException {
        LogFile logFile = logParserService.uploadAndParse(file, principal.getName());
        return ResponseEntity.ok(logFile);
    }

    @Operation(summary = "Get all uploaded log files")
    @GetMapping
    public ResponseEntity<List<LogFile>> getAllLogs() {
        return ResponseEntity.ok(logParserService.getAllLogFiles());
    }

    @Operation(summary = "Get parsed events for a log file")
    @GetMapping("/{id}/events")
    public ResponseEntity<List<LogEvent>> getLogEvents(@PathVariable Long id) {
        return ResponseEntity.ok(logParserService.getEventsForLogFile(id));
    }
}