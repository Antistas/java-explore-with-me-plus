package ru.practicum.gateway.compilation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.gateway.client.BaseClient;
import java.util.Map;

@Service
public class CompilationClient extends BaseClient {
    public CompilationClient(@Value("${ewm-main-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> getCompilations(Boolean pinned, int from, int size) {

        UriComponentsBuilder uri = UriComponentsBuilder
                .fromPath("/compilations")
                .queryParam("from", from)
                .queryParam("size", size);

        if (pinned != null) {
            uri.queryParam("pinned", pinned);
        }

        return get(uri.build().toUriString());
    }

    public ResponseEntity<Object> getCompilation(long compilationId) {
        return get("/compilations/{compilationId}", null, Map.of("compilationId", compilationId));
    }
}
