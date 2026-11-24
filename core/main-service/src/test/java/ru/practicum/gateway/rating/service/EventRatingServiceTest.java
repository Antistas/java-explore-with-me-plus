package ru.practicum.gateway.rating.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.repository.EventRepository;
import ru.practicum.gateway.rating.model.EventRating;
import ru.practicum.gateway.rating.repository.EventRatingRepository;
import ru.practicum.gateway.user.model.User;
import ru.practicum.gateway.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRatingServiceTest {
    @Mock
    private EventRatingRepository ratingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventRatingService ratingService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        event = Event.builder().id(2L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
    }

    @Test
    void likeShouldCreateRating() {
        when(ratingRepository.findByEventIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        ratingService.toggleLike(1L, 2L);

        verify(ratingRepository).save(any(EventRating.class));
    }

    @Test
    void repeatedLikeShouldDeleteRating() {
        EventRating current = EventRating.builder().event(event).user(user).rating((short) 1).build();
        when(ratingRepository.findByEventIdAndUserId(2L, 1L)).thenReturn(Optional.of(current));

        ratingService.toggleLike(1L, 2L);

        verify(ratingRepository).delete(current);
        verify(ratingRepository, never()).save(current);
    }

    @Test
    void dislikeShouldReplaceLike() {
        EventRating current = EventRating.builder().event(event).user(user).rating((short) 1).build();
        when(ratingRepository.findByEventIdAndUserId(2L, 1L)).thenReturn(Optional.of(current));

        ratingService.toggleDislike(1L, 2L);

        assertEquals((short) -1, current.getRating());
        verify(ratingRepository).save(current);
        verify(ratingRepository, never()).delete(current);
    }
}
