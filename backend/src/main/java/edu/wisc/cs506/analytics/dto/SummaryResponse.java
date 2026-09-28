package edu.wisc.cs506.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SummaryResponse(ProjectInfo project, String source, Instant syncedAt,
        int mergedCount, BigDecimal averageMergeTimeHours,
        BigDecimal fastestMergeTimeHours, BigDecimal slowestMergeTimeHours) {
    public static SummaryResponse from(AnalyticsResponse response) {
        MergeRequestSummary summary = response.summary();
        return new SummaryResponse(response.project(), response.source(), response.syncedAt(),
                summary.mergedCount(), summary.averageMergeTimeHours(),
                summary.fastestMergeTimeHours(), summary.slowestMergeTimeHours());
    }
}
