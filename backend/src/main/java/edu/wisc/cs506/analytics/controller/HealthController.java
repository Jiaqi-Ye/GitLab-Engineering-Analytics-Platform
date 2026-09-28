package edu.wisc.cs506.analytics.controller;

import edu.wisc.cs506.analytics.repository.AnalyticsRepository;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final AnalyticsRepository repository;

    public HealthController(AnalyticsRepository repository) { this.repository = repository; }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        boolean healthy;
        try {
            healthy = repository.isHealthy();
        } catch (RuntimeException exception) {
            healthy = false;
        }
        return healthy ? ResponseEntity.ok(Map.of("status", "UP", "database", "UP"))
                : ResponseEntity.status(503).body(Map.of("status", "DOWN", "database", "DOWN"));
    }
}
