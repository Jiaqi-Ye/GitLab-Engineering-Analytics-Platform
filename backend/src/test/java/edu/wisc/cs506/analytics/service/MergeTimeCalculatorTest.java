package edu.wisc.cs506.analytics.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class MergeTimeCalculatorTest {
    private final JsonMapper mapper = JsonMapper.builder().build();
    private final MergeTimeCalculator calculator = new MergeTimeCalculator();

    @Test
    void matchesExistingPythonCalculationAcrossUtcOffsets() {
        var records = mapper.readTree("""
                [{"iid":13,"title":"Timezone example","created_at":"2026-09-23T10:00:00-05:00",
                  "merged_at":"2026-09-23T18:30:00Z","web_url":null}]
                """);
        var metrics = calculator.calculate(records);
        assertEquals(new BigDecimal("3.50"), metrics.get(0).mergeTimeHours());
        assertEquals("2026-09-23T15:00:00Z", metrics.get(0).createdAt().toString());
    }

    @Test
    void skipsUnmergedRecordsAndRepresentsEmptySummaryWithNullMetrics() {
        var metrics = calculator.calculate(mapper.readTree("[{\"iid\":14,\"merged_at\":null}]"));
        assertTrue(metrics.isEmpty());
        var summary = calculator.summarize(metrics);
        assertEquals(0, summary.mergedCount());
        assertNull(summary.averageMergeTimeHours());
        assertNull(summary.fastestMergeTimeHours());
        assertNull(summary.slowestMergeTimeHours());
    }

    @Test
    void rejectsMissingTimezoneAndNegativeDuration() {
        for (String created : List.of("2026-09-23T10:00:00", "2026-09-23T12:00:00Z")) {
            var records = mapper.readTree("""
                    [{"iid":17,"title":"Invalid time","created_at":"%s",
                      "merged_at":"2026-09-23T11:00:00Z"}]
                    """.formatted(created));
            assertThrows(AnalyticsException.class, () -> calculator.calculate(records));
        }
    }

    @Test
    void demoFixtureUsesSamePipelineAndHasKnownSummary() throws Exception {
        try (var fixture = getClass().getResourceAsStream("/demo-merge-requests.json")) {
            var summary = calculator.summarize(calculator.calculate(mapper.readTree(fixture)));
            assertEquals(6, summary.mergedCount());
            assertEquals(new BigDecimal("8.33"), summary.averageMergeTimeHours());
            assertEquals(new BigDecimal("0.75"), summary.fastestMergeTimeHours());
            assertEquals(new BigDecimal("24.00"), summary.slowestMergeTimeHours());
        }
    }
}
