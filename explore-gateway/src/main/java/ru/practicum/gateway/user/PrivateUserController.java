package ru.practicum.gateway.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{userId}/event/{eventId}")
@RequiredArgsConstructor
@Validated
public class PrivateUserController {
    private final PrivateUserClient userClient;

    @PostMapping("/like")
    public ResponseEntity<Object> toggleLike(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long eventId) {
        return userClient.toggleLike(userId, eventId);
    }

    @PostMapping("/dislike")
    public ResponseEntity<Object> toggleDislike(@PathVariable @Positive Long userId,
                                                @PathVariable @Positive Long eventId) {
        return userClient.toggleDislike(userId, eventId);
    }
}
