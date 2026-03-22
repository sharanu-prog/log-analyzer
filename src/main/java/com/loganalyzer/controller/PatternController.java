package com.loganalyzer.controller;

import com.loganalyzer.model.FailurePattern;
import com.loganalyzer.service.PatternDetectionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class PatternController {

    private final PatternDetectionService patternDetectionService;

    // 🧠 POST /api/patterns/analyze — Run pattern detection
    @PostMapping("/analyze")
    public ResponseEntity<List<FailurePattern>> analyzePatterns() {
        List<FailurePattern> patterns = patternDetectionService.detectPatterns();
        return ResponseEntity.ok(patterns);
    }

    // 📋 GET /api/patterns — Get all detected patterns
    @GetMapping
    public ResponseEntity<List<FailurePattern>> getAllPatterns() {
        return ResponseEntity.ok(patternDetectionService.getAllPatterns());
    }

    // 📋 GET /api/patterns/level/{level} — Get patterns by level
    @GetMapping("/level/{level}")
    public ResponseEntity<List<FailurePattern>> getByLevel(@PathVariable String level) {
        return ResponseEntity.ok(patternDetectionService.getPatternsByLevel(level.toUpperCase()));
    }
}