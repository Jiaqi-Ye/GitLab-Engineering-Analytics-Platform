package edu.wisc.cs506.analytics.dto;

import edu.wisc.cs506.analytics.model.MergeRequestMetric;
import java.time.Instant;
import java.util.List;

public record AnalyticsResponse(ProjectInfo project, String source, Instant syncedAt,
        List<MergeRequestMetric> mergeRequests, MergeRequestSummary summary) {}
