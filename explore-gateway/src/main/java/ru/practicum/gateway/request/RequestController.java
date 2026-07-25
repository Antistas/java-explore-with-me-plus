package ru.practicum.gateway.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
public class RequestController {


    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<Object> getUserRequestsPrivate(@PathVariable Long userId) {
        log.info("Gateway stub: GET /users/{}/requests", userId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<Object> addParticipationRequestPrivate(
            @PathVariable Long userId,
            @RequestParam Long eventId) {

        log.info("Gateway stub: POST /users/{}/requests | eventId={}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequestPrivate(
            @PathVariable Long userId,
            @PathVariable Long requestId) {

        log.info("Gateway stub: PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return ResponseEntity.ok().build();
    }
}