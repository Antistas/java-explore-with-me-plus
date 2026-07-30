package ru.practicum.gateway.category;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class CategoryClient extends BaseClient {
    public CategoryClient(
            @Value("${ewm-main-service.url}") String serverUrl,
            RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> getCategories(int from, int size) {
        return get("/categories?from={from}&size={size}", null, Map.of("from", from, "size", size)
        );
    }

    public ResponseEntity<Object> getCategory(long categoryId) {
        return get("/categories/{categoryId}", null, Map.of("categoryId", categoryId));
    }
}
