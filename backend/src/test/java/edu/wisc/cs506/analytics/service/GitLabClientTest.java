package edu.wisc.cs506.analytics.service;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import edu.wisc.cs506.analytics.config.GitLabConfig;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class GitLabClientTest {
    @Test
    void followsGitLabPaginationAndSendsTokenServerSide() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        List<String> seen = new ArrayList<>();
        server.createContext("/api/v4/projects/158108/merge_requests", exchange -> {
            seen.add(exchange.getRequestHeaders().getFirst("PRIVATE-TOKEN"));
            String query = exchange.getRequestURI().getQuery();
            boolean first = query.endsWith("&page=1");
            byte[] body = (first ? "[{\"iid\":1}]" : "[{\"iid\":2}]").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("X-Next-Page", first ? "2" : "");
            exchange.sendResponseHeaders(200, body.length);
            try (var stream = exchange.getResponseBody()) { stream.write(body); }
        });
        server.start();
        try {
            var client = client(server);
            assertEquals(2, client.fetchMergeRequests().size());
            assertEquals(List.of("test-only-not-a-real-secret", "test-only-not-a-real-secret"), seen);
        } finally { server.stop(0); }
    }

    @Test
    void upstreamBodyIsNeverExposedByFailure() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = "private-upstream-diagnostic".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(401, body.length);
            try (var stream = exchange.getResponseBody()) { stream.write(body); }
        });
        server.start();
        try {
            var exception = assertThrows(AnalyticsException.class, () -> client(server).fetchMergeRequests());
            assertEquals(502, exception.status());
            assertTrue(exception.getMessage().contains("401"));
            assertFalse(exception.getMessage().contains("private-upstream-diagnostic"));
            assertFalse(exception.getMessage().contains("test-only-not-a-real-secret"));
        } finally { server.stop(0); }
    }

    private GitLabClient client(HttpServer server) {
        var config = new GitLabConfig("live", "http://127.0.0.1:" + server.getAddress().getPort(),
                "158108", "T29", "", "test-only-not-a-real-secret");
        return new GitLabClient(config, JsonMapper.builder().build());
    }
}
