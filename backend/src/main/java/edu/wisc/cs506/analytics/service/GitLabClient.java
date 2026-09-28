package edu.wisc.cs506.analytics.service;

import edu.wisc.cs506.analytics.config.GitLabConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Component
public class GitLabClient {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES = 100;
    private final GitLabConfig config;
    private final ObjectMapper mapper;
    private final HttpClient http;

    public GitLabClient(GitLabConfig config, ObjectMapper mapper) {
        this.config = config;
        this.mapper = mapper;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER).build();
    }

    public JsonNode fetchMergeRequests() {
        if (config.isDemo()) {
            try (InputStream fixture = new ClassPathResource("demo-merge-requests.json").getInputStream()) {
                return mapper.readTree(fixture);
            } catch (IOException exception) {
                throw new AnalyticsException(500, "The demo fixture could not be loaded.");
            }
        }
        ArrayNode all = mapper.createArrayNode();
        for (int page = 1; page <= MAX_PAGES; page++) {
            URI uri = URI.create(config.baseUrl() + "/api/v4/projects/" + config.project().id()
                    + "/merge_requests?state=merged&scope=all&per_page=" + PAGE_SIZE + "&page=" + page);
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/json").GET();
            if (!config.token().isBlank()) {
                builder.header("PRIVATE-TOKEN", config.token());
            }
            HttpResponse<String> response;
            try {
                response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AnalyticsException(502, "GitLab synchronization was interrupted. Existing cached data was preserved.");
            } catch (IOException exception) {
                throw new AnalyticsException(502, "GitLab could not be reached. Check the server URL and network. Existing cached data was preserved.");
            }
            if (response.statusCode() != 200) {
                // Never return the upstream body, URL, or headers: they may contain secrets.
                throw new AnalyticsException(502,
                        "GitLab returned HTTP " + response.statusCode()
                        + ". Check the project ID and server-side token permissions. Existing cached data was preserved.");
            }
            JsonNode records;
            try {
                records = mapper.readTree(response.body());
            } catch (RuntimeException exception) {
                throw new AnalyticsException(502, "GitLab returned invalid JSON. Existing cached data was preserved.");
            }
            if (records == null || !records.isArray()) {
                throw new AnalyticsException(502, "GitLab returned an unexpected response. Existing cached data was preserved.");
            }
            all.addAll((ArrayNode) records);
            var nextHeader = response.headers().firstValue("X-Next-Page");
            if (nextHeader.isPresent()) {
                String next = nextHeader.get().trim();
                if (next.isEmpty()) {
                    return all;
                }
                if (!next.equals(String.valueOf(page + 1))) {
                    throw new AnalyticsException(502, "GitLab pagination was inconsistent. Existing cached data was preserved.");
                }
            } else if (records.size() < PAGE_SIZE) {
                return all;
            }
        }
        throw new AnalyticsException(502, "GitLab exceeded the 10,000 merge request prototype limit. No partial data was saved.");
    }
}
