package edu.wisc.cs506.analytics.config;

import edu.wisc.cs506.analytics.dto.ProjectInfo;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitLabConfig {
    private final String mode;
    private final String baseUrl;
    private final String token;
    private final ProjectInfo project;

    public GitLabConfig(@Value("${analytics.gitlab.mode}") String mode,
            @Value("${analytics.gitlab.base-url}") String baseUrl,
            @Value("${analytics.gitlab.project-id}") String projectId,
            @Value("${analytics.gitlab.project-name}") String projectName,
            @Value("${analytics.gitlab.project-url}") String projectUrl,
            @Value("${analytics.gitlab.token}") String token) {
        if (!mode.equals("demo") && !mode.equals("live")) {
            throw new IllegalArgumentException("GITLAB_MODE must be demo or live.");
        }
        URI uri = URI.create(baseUrl);
        if (!("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))
                || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getQuery() != null || uri.getFragment() != null) {
            throw new IllegalArgumentException("GITLAB_BASE_URL must be an HTTP(S) origin without credentials.");
        }
        if (!projectId.matches("[0-9]+")) {
            throw new IllegalArgumentException("GITLAB_PROJECT_ID must be a numeric GitLab project ID.");
        }
        this.mode = mode;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.token = token;
        this.project = new ProjectInfo(projectId, projectName,
                projectUrl.isBlank() ? this.baseUrl + "/projects/" + projectId : projectUrl);
    }

    public boolean isDemo() { return mode.equals("demo"); }
    public String source() { return isDemo() ? "demo" : "gitlab"; }
    public String baseUrl() { return baseUrl; }
    public String token() { return token; }
    public ProjectInfo project() { return project; }

    // Prevent demo data, different projects, or different GitLab hosts sharing a cache.
    public String snapshotKey() {
        try {
            byte[] value = (source() + "\n" + baseUrl + "\n" + project.id()).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.");
        }
    }
}
