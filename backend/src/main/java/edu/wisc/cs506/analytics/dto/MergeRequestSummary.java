package edu.wisc.cs506.analytics.dto;

import java.math.BigDecimal;

public record MergeRequestSummary(int mergedCount, BigDecimal averageMergeTimeHours,
        BigDecimal fastestMergeTimeHours, BigDecimal slowestMergeTimeHours) {}
