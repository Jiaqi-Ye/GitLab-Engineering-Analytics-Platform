package edu.wisc.cs506.analytics.repository;

import edu.wisc.cs506.analytics.dto.ProjectInfo;
import edu.wisc.cs506.analytics.model.AnalyticsSnapshot;
import edu.wisc.cs506.analytics.model.MergeRequestMetric;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AnalyticsRepository {
    private final JdbcTemplate jdbc;

    public AnalyticsRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public boolean isHealthy() {
        return Integer.valueOf(1).equals(jdbc.queryForObject("SELECT 1", Integer.class));
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Optional<AnalyticsSnapshot> find(String snapshotKey) {
        List<AnalyticsSnapshot> snapshots = jdbc.query("""
                SELECT project_id, project_name, project_url, source, synced_at
                FROM analytics_snapshots WHERE snapshot_key = ?
                """, (rs, row) -> new AnalyticsSnapshot(
                        new ProjectInfo(rs.getString("project_id"), rs.getString("project_name"),
                                rs.getString("project_url")), rs.getString("source"),
                        rs.getTimestamp("synced_at", utc()).toInstant(), List.of()), snapshotKey);
        if (snapshots.isEmpty()) {
            return Optional.empty();
        }
        List<MergeRequestMetric> metrics = jdbc.query("""
                SELECT iid, title, created_at, merged_at, merge_time_hours, web_url
                FROM merge_request_analytics WHERE snapshot_key = ? ORDER BY merged_at DESC, iid
                """, (rs, row) -> new MergeRequestMetric(rs.getInt("iid"), rs.getString("title"),
                        rs.getTimestamp("created_at", utc()).toInstant(),
                        rs.getTimestamp("merged_at", utc()).toInstant(),
                        rs.getBigDecimal("merge_time_hours"), rs.getString("web_url")), snapshotKey);
        AnalyticsSnapshot snapshot = snapshots.get(0);
        return Optional.of(new AnalyticsSnapshot(snapshot.project(), snapshot.source(),
                snapshot.syncedAt(), List.copyOf(metrics)));
    }

    @Transactional
    public void replace(String snapshotKey, AnalyticsSnapshot snapshot) {
        jdbc.update(connection -> {
            var statement = connection.prepareStatement("""
                    INSERT INTO analytics_snapshots
                        (snapshot_key, project_id, project_name, project_url, source, synced_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE project_id = VALUES(project_id), project_name = VALUES(project_name),
                        project_url = VALUES(project_url), source = VALUES(source), synced_at = VALUES(synced_at)
                    """);
            statement.setString(1, snapshotKey);
            statement.setString(2, snapshot.project().id());
            statement.setString(3, snapshot.project().name());
            statement.setString(4, snapshot.project().url());
            statement.setString(5, snapshot.source());
            statement.setTimestamp(6, Timestamp.from(snapshot.syncedAt()), utc());
            return statement;
        });
        jdbc.update("DELETE FROM merge_request_analytics WHERE snapshot_key = ?", snapshotKey);
        jdbc.batchUpdate("""
                INSERT INTO merge_request_analytics
                    (snapshot_key, iid, title, created_at, merged_at, merge_time_hours, web_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, snapshot.mergeRequests(), 100, (statement, metric) -> {
                    statement.setString(1, snapshotKey);
                    statement.setInt(2, metric.iid());
                    statement.setString(3, metric.title());
                    statement.setTimestamp(4, Timestamp.from(metric.createdAt()), utc());
                    statement.setTimestamp(5, Timestamp.from(metric.mergedAt()), utc());
                    statement.setBigDecimal(6, metric.mergeTimeHours());
                    statement.setString(7, metric.webUrl());
                });
    }

    private static Calendar utc() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }
}
