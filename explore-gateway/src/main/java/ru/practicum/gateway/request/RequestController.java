package ru.practicum.gateway.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    //авторизованый пользователь
    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<Object> getUserRequestsPrivate(
            @PathVariable @Positive Long userId) {
        log.info("GET /users/{}/requests - получение заявок пользователя", userId);
        return requestClient.getUserRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<Object> addParticipationRequestPrivate(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId) {
        log.info("POST /users/{}/requests?eventId={} - создание заявки на участие", userId, eventId);
        return requestClient.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequestPrivate(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel - отмена заявки", userId, requestId);
        return requestClient.cancelRequest(userId, requestId);
    }
}