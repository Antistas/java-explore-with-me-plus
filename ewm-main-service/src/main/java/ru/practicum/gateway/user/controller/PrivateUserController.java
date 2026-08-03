package ru.practicum.gateway.user.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.rating.service.EventRatingService;

@RestController
@RequestMapping("/user/{userId}/event/{eventId}")
@RequiredArgsConstructor
@Validated
public class PrivateUserController {
    private final EventRatingService ratingService;

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleLike(@PathVariable @Positive Long userId,
                           @PathVariable @Positive Long eventId) {
        ratingService.toggleLike(userId, eventId);
    }

    @PostMapping("/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleDislike(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long eventId) {
        ratingService.toggleDislike(userId, eventId);
    }
}
