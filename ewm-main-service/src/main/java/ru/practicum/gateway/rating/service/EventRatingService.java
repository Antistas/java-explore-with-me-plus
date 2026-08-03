package ru.practicum.gateway.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.repository.EventRepository;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.rating.dto.EventRatingSum;
import ru.practicum.gateway.rating.model.EventRating;
import ru.practicum.gateway.rating.repository.EventRatingRepository;
import ru.practicum.gateway.user.model.User;
import ru.practicum.gateway.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventRatingService {
    private static final short LIKE = 1;
    private static final short DISLIKE = -1;

    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleLike(Long userId, Long eventId) {
        toggle(userId, eventId, LIKE);
    }

    @Transactional
    public void toggleDislike(Long userId, Long eventId) {
        toggle(userId, eventId, DISLIKE);
    }

    public long getRating(Long eventId) {
        return getRatingsByIds(List.of(eventId)).getOrDefault(eventId, 0L);
    }

    public Map<Long, Long> getRatings(List<Event> events) {
        return getRatingsByIds(events.stream().map(Event::getId).toList());
    }

    private void toggle(Long userId, Long eventId, short value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        ratingRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresentOrElse(current -> {
                    if (current.getRating() == value) {
                        ratingRepository.delete(current);
                    } else {
                        current.setRating(value);
                        ratingRepository.save(current);
                    }
                }, () -> ratingRepository.save(EventRating.builder()
                        .event(event)
                        .user(user)
                        .rating(value)
                        .build()));
    }

    private Map<Long, Long> getRatingsByIds(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }
        return ratingRepository.sumByEventIds(eventIds).stream()
                .collect(Collectors.toMap(EventRatingSum::eventId, EventRatingSum::rating));
    }
}
