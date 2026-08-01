package ru.practicum.stats.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void saveHit(String app, String uri, String ip) {
        EndpointHit hit = EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
    }

    public void saveHit(EndpointHit hit) {
        restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        UriComponentsBuilder urlBuild = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr);
        if (uris != null && !uris.isEmpty()) {
            urlBuild.queryParam("uris", uris.toArray());
        }

        if (Boolean.TRUE.equals(unique)) {
            urlBuild.queryParam("unique", true);
        }

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                urlBuild.build().encode().toUri(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody() == null ? List.of() : response.getBody();
    }
}
