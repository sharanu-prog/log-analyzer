package com.loganalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogAnalyzerApplication.class, args);
        System.out.println("✅ Log Analyzer Backend running at http://localhost:8080");
        System.out.println("📄 Swagger UI at http://localhost:8080/swagger-ui.html");
    }
}