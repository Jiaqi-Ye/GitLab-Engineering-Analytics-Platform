package edu.wisc.cs506.analytics.model;

import java.math.BigDecimal;
import java.time.Instant;

public record MergeRequestMetric(int iid, String title, Instant createdAt, Instant mergedAt,
        BigDecimal mergeTimeHours, String webUrl) {}
