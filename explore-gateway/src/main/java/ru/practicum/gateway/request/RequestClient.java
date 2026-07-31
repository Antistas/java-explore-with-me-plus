package ru.practicum.gateway.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    public RequestClient(@Value("${ewm-main-service.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     * GET /users/{userId}/requests
     */
    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/users/{userId}/requests", userId, Map.of("userId", userId));
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     * POST /users/{userId}/requests?eventId={eventId}
     */
    public ResponseEntity<Object> addParticipationRequest(Long userId, Long eventId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "eventId", eventId
        );
        return post("/users/{userId}/requests?eventId={eventId}", userId, parameters, null);
    }

    /**
     * Отмена своего запроса на участие в событии
     * PATCH /users/{userId}/requests/{requestId}/cancel
     */
    public ResponseEntity<Object> cancelRequest(Long userId, Long requestId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId,
                "requestId", requestId
        );
        return patch("/users/{userId}/requests/{requestId}/cancel", userId, parameters, null);
    }
}