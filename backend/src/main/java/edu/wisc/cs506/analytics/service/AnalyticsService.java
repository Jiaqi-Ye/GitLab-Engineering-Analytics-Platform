package edu.wisc.cs506.analytics.service;

import edu.wisc.cs506.analytics.config.GitLabConfig;
import edu.wisc.cs506.analytics.dto.AnalyticsResponse;
import edu.wisc.cs506.analytics.model.AnalyticsSnapshot;
import edu.wisc.cs506.analytics.repository.AnalyticsRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final GitLabConfig config;
    private final AnalyticsRepository repository;
    private final GitLabClient client;
    private final MergeTimeCalculator calculator;

    public AnalyticsService(GitLabConfig config, AnalyticsRepository repository,
            GitLabClient client, MergeTimeCalculator calculator) {
        this.config = config;
        this.repository = repository;
        this.client = client;
        this.calculator = calculator;
    }

    // Serialize first load and refresh in the single backend instance used by this prototype.
    public synchronized AnalyticsResponse getAnalytics() {
        return repository.find(config.snapshotKey()).map(this::response).orElseGet(this::sync);
    }

    public synchronized AnalyticsResponse sync() {
        // Fetch and validate everything before the transactional replacement starts.
        var metrics = calculator.calculate(client.fetchMergeRequests());
        var snapshot = new AnalyticsSnapshot(config.project(), config.source(),
                Instant.now().truncatedTo(ChronoUnit.MICROS), metrics);
        repository.replace(config.snapshotKey(), snapshot);
        // Read back from MySQL, including on the first request, to exercise the full data path.
        return response(repository.find(config.snapshotKey()).orElseThrow(() ->
                new AnalyticsException(503, "The saved analytics snapshot could not be read from MySQL.")));
    }

    private AnalyticsResponse response(AnalyticsSnapshot snapshot) {
        return new AnalyticsResponse(snapshot.project(), snapshot.source(), snapshot.syncedAt(),
                snapshot.mergeRequests(), calculator.summarize(snapshot.mergeRequests()));
    }
}
