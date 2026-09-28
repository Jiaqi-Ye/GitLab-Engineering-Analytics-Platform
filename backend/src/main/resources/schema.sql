CREATE TABLE IF NOT EXISTS analytics_snapshots (
    snapshot_key CHAR(64) NOT NULL PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    project_url TEXT NOT NULL,
    source VARCHAR(16) NOT NULL,
    synced_at DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS merge_request_analytics (
    snapshot_key CHAR(64) NOT NULL,
    iid INT NOT NULL,
    title TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    merged_at DATETIME(6) NOT NULL,
    merge_time_hours DECIMAL(16,2) NOT NULL,
    web_url TEXT NULL,
    PRIMARY KEY (snapshot_key, iid),
    CONSTRAINT fk_merge_request_snapshot FOREIGN KEY (snapshot_key)
        REFERENCES analytics_snapshots(snapshot_key) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
