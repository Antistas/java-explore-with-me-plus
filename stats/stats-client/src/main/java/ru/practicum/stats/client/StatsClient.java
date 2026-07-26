package ru.practicum.stats.client;

import org.springframework.web.client.RestClient;
import ru.practicum.stats.dto.EndpointHit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;

    public StatsClient(String serverUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public void saveHit(String app, String uri, String ip) {
        EndpointHit hit = new EndpointHit(app, uri, ip, LocalDateTime.now().format(FORMATTER));

        restClient.post()
                .uri("/hit")
                .body(hit)
                .retrieve()
                .toBodilessEntity();
    }
}
