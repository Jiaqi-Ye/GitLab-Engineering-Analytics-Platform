package edu.wisc.cs506.analytics.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.wisc.cs506.analytics.config.GitLabConfig;
import edu.wisc.cs506.analytics.model.AnalyticsSnapshot;
import edu.wisc.cs506.analytics.repository.AnalyticsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class AnalyticsServiceTest {
    private final GitLabConfig config = new GitLabConfig("demo", "https://gitlab.example.com", "158108", "T29", "", "");
    private final AnalyticsRepository repository = mock(AnalyticsRepository.class);
    private final GitLabClient client = mock(GitLabClient.class);
    private final AnalyticsService service = new AnalyticsService(config, repository, client, new MergeTimeCalculator());

    @Test
    void anEmptyPersistedSnapshotDoesNotRefetchGitLab() {
        var snapshot = new AnalyticsSnapshot(config.project(), "demo", Instant.parse("2026-09-28T12:00:00Z"), List.of());
        when(repository.find(config.snapshotKey())).thenReturn(Optional.of(snapshot));
        assertEquals(0, service.getAnalytics().summary().mergedCount());
        verifyNoInteractions(client);
        verify(repository, never()).replace(anyString(), any());
    }

    @Test
    void upstreamFailureDoesNotReplaceExistingSnapshot() {
        when(client.fetchMergeRequests()).thenThrow(new AnalyticsException(502, "GitLab unavailable."));
        assertThrows(AnalyticsException.class, service::sync);
        verify(repository, never()).replace(anyString(), any());
    }

    @Test
    void firstGetPersistsEvenEmptyResultsAndReadsThemBack() {
        var snapshot = new AnalyticsSnapshot(config.project(), "demo", Instant.parse("2026-09-28T12:00:00Z"), List.of());
        when(repository.find(config.snapshotKey())).thenReturn(Optional.empty(), Optional.of(snapshot));
        when(client.fetchMergeRequests()).thenReturn(JsonMapper.builder().build().createArrayNode());
        assertEquals(snapshot.syncedAt(), service.getAnalytics().syncedAt());
        verify(repository).replace(eq(config.snapshotKey()), any());
        verify(repository, times(2)).find(config.snapshotKey());
    }

    @Test
    void demoAndLiveHaveDifferentDatabaseKeys() {
        var live = new GitLabConfig("live", "https://gitlab.example.com", "158108", "T29", "", "");
        assertNotEquals(config.snapshotKey(), live.snapshotKey());
    }
}
