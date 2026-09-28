package edu.wisc.cs506.analytics.service;

import edu.wisc.cs506.analytics.dto.MergeRequestSummary;
import edu.wisc.cs506.analytics.model.MergeRequestMetric;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class MergeTimeCalculator {
    /** Java integration of gitlab_analytics/merge_time.py's timezone-aware calculation. */
    public List<MergeRequestMetric> calculate(JsonNode records) {
        if (records == null || !records.isArray()) {
            throw invalidData();
        }
        List<MergeRequestMetric> metrics = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (JsonNode record : records) {
            JsonNode iid = record.get("iid");
            if (!record.isObject() || iid == null || !iid.isIntegralNumber()
                    || !iid.canConvertToInt() || iid.intValue() <= 0) {
                throw invalidData();
            }
            JsonNode mergedValue = record.get("merged_at");
            if (mergedValue == null || mergedValue.isNull()) {
                continue;
            }
            if (!seen.add(iid.intValue())) {
                throw invalidData();
            }
            Instant created = timestamp(record.get("created_at"));
            Instant merged = timestamp(mergedValue);
            Duration duration = Duration.between(created, merged);
            if (duration.isNegative()) {
                throw invalidData();
            }
            // Decimal HALF_EVEN avoids binary-float rounding artifacts (2.675h becomes 2.68h).
            BigDecimal hours = BigDecimal.valueOf(duration.getSeconds())
                    .add(BigDecimal.valueOf(duration.getNano(), 9))
                    .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_EVEN);
            JsonNode title = record.get("title");
            if (title == null || !title.isTextual()) {
                throw invalidData();
            }
            JsonNode webUrl = record.get("web_url");
            if (webUrl != null && !webUrl.isNull() && !webUrl.isTextual()) {
                throw invalidData();
            }
            metrics.add(new MergeRequestMetric(iid.intValue(), title.asText(), created,
                    merged, hours, webUrl == null || webUrl.isNull() ? null : webUrl.asText()));
        }
        metrics.sort(Comparator.comparing(MergeRequestMetric::mergedAt).reversed()
                .thenComparing(MergeRequestMetric::iid));
        return List.copyOf(metrics);
    }

    public MergeRequestSummary summarize(List<MergeRequestMetric> metrics) {
        if (metrics.isEmpty()) {
            return new MergeRequestSummary(0, null, null, null);
        }
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = metrics.get(0).mergeTimeHours();
        BigDecimal max = min;
        for (MergeRequestMetric metric : metrics) {
            sum = sum.add(metric.mergeTimeHours());
            min = min.min(metric.mergeTimeHours());
            max = max.max(metric.mergeTimeHours());
        }
        return new MergeRequestSummary(metrics.size(),
                sum.divide(BigDecimal.valueOf(metrics.size()), 2, RoundingMode.HALF_EVEN), min, max);
    }

    private Instant timestamp(JsonNode value) {
        if (value == null || !value.isTextual()) {
            throw invalidData();
        }
        try {
            return OffsetDateTime.parse(value.asText()).toInstant();
        } catch (DateTimeParseException exception) {
            throw invalidData();
        }
    }

    private AnalyticsException invalidData() {
        return new AnalyticsException(502, "Merge request data has invalid identifiers or timezone-aware timestamps. Existing cached data was preserved.");
    }
}
