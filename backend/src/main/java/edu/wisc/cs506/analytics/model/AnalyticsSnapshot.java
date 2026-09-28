package edu.wisc.cs506.analytics.model;

import edu.wisc.cs506.analytics.dto.ProjectInfo;
import java.time.Instant;
import java.util.List;

public record AnalyticsSnapshot(ProjectInfo project, String source, Instant syncedAt,
        List<MergeRequestMetric> mergeRequests) {}
