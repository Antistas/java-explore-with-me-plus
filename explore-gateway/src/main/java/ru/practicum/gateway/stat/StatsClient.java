package ru.practicum.gateway.stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.stat.dto.EndpointHitDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> saveHit(EndpointHitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);

        StringBuilder pathBuilder = new StringBuilder("/stats?start={start}&end={end}");

        if (uris != null && !uris.isEmpty()) {
            // Превращаем список в строку с разделителем-запятой для передачи в query-параметре
            parameters.put("uris", String.join(",", uris));
            pathBuilder.append("&uris={uris}");
        }

        if (unique != null) {
            parameters.put("unique", unique);
            pathBuilder.append("&unique={unique}");
        }

        return get(pathBuilder.toString(), null, parameters);
    }
}