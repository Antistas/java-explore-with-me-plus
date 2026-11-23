package ru.practicum.stats.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long RETRY_BACK_OFF_MS = 3000L;
    private static final int RETRY_MAX_ATTEMPTS = 3;

    private final DiscoveryClient discoveryClient;
    private final String statsServiceId;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    public StatsClient(DiscoveryClient discoveryClient, String statsServiceId) {
        this(discoveryClient, statsServiceId, new RestTemplate(), defaultRetryTemplate());
    }

    public StatsClient(DiscoveryClient discoveryClient,
                       String statsServiceId,
                       RestTemplate restTemplate,
                       RetryTemplate retryTemplate) {
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    public void saveHit(String app, String uri, String ip) {
        EndpointHit hit = EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        saveHit(hit);
    }

    public void saveHit(EndpointHit hit) {
        restTemplate.postForEntity(makeUri("/hit"), hit, Void.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        UriComponentsBuilder urlBuild = UriComponentsBuilder.fromUri(makeUri("/stats"))
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER));
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

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(context -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
                    exception
            );
        }
    }

    private static RetryTemplate defaultRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(RETRY_BACK_OFF_MS);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(RETRY_MAX_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
