package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class PrivateUserClient extends BaseClient {
    public PrivateUserClient(@Value("${ewm-main-service.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> toggleLike(long userId, long eventId) {
        return post("/user/{userId}/event/{eventId}/like", userId,
                Map.of("userId", userId, "eventId", eventId), null);
    }

    public ResponseEntity<Object> toggleDislike(long userId, long eventId) {
        return post("/user/{userId}/event/{eventId}/dislike", userId,
                Map.of("userId", userId, "eventId", eventId), null);
    }
}
