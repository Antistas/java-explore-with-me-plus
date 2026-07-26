package ru.practicum.stats_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class StatsClientTests {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SERVER_URL = "http://localhost:9090";

    private ObjectMapper objectMapper;
    private StatsClient statsClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        statsClient = new StatsClient(SERVER_URL, restTemplate);
        objectMapper = new ObjectMapper();
    }

    @Test
    void saveHit_ShouldSendPostRequest() throws Exception {
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.168.0.1";

        EndpointHit hit = EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        String expectedJson = objectMapper.writeValueAsString(hit);

        mockServer.expect(ExpectedCount.once(), requestTo(SERVER_URL + "/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andRespond(withStatus(HttpStatus.CREATED));

        statsClient.saveHit(app, uri, ip);

        mockServer.verify();
    }

    @Test
    void saveHit_ShouldHandleServerError() {
        mockServer.expect(ExpectedCount.once(), requestTo(SERVER_URL + "/hit"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            statsClient.saveHit("app", "/uri", "ip");
        });

        mockServer.verify();
    }

    @Test
    void getStats_WithoutUris_ShouldSendGetRequest() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00&end=2026-07-24%2023:59:59";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"app\":\"app1\",\"uri\":\"/uri1\",\"hits\":10}]"));

        List<ViewStats> result = statsClient.getStats(start, end, null, false);

        assertThat(result).isNotEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WithUris_ShouldIncludeUrisInUrl() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);
        List<String> uris = List.of("/events/1", "/events/2");

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59" +
                "&uris=" + uris.get(0) + "&uris=" + uris.get(1);

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, uris, false);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WithUniqueTrue_ShouldIncludeUniqueParam() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59" +
                "&unique=true";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, null, true);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WithUrisAndUniqueTrue_ShouldIncludeBothParams() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);
        List<String> uris = List.of("/events/1");

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59" +
                "&uris=" + uris.get(0) +
                "&unique=true";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, uris, true);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WithEmptyUris_ShouldNotIncludeUrisParam() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);
        List<String> uris = List.of();

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, uris, false);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WithMultipleUris_ShouldBuildCorrectUrl() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);
        List<String> uris = List.of("/events/1", "/events/2", "/events/3");

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59" +
                "&uris=" + uris.get(0) + "&uris=" + uris.get(1) + "&uris=" + uris.get(2);

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, uris, false);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WhenServerReturnsEmptyList_ShouldReturnEmptyList() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        List<ViewStats> result = statsClient.getStats(start, end, null, false);

        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    void getStats_WhenServerReturnsError_ShouldThrowException() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 24, 23, 59, 59);

        String expectedUrl = SERVER_URL + "/stats?start=2026-07-24%2000:00:00" +
                "&end=2026-07-24%2023:59:59";

        mockServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            statsClient.getStats(start, end, null, false);
        });

        mockServer.verify();
    }
}