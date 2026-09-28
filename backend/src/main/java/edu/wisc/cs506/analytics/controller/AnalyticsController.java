package edu.wisc.cs506.analytics.controller;

import edu.wisc.cs506.analytics.dto.AnalyticsResponse;
import edu.wisc.cs506.analytics.dto.SummaryResponse;
import edu.wisc.cs506.analytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/merge-requests")
public class AnalyticsController {
    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) { this.service = service; }

    @GetMapping
    public AnalyticsResponse analytics() { return service.getAnalytics(); }

    @GetMapping("/summary")
    public SummaryResponse summary() { return SummaryResponse.from(service.getAnalytics()); }

    @PostMapping("/sync")
    public AnalyticsResponse sync() { return service.sync(); }
}
