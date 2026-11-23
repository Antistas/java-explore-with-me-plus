package ru.practicum.gateway.rating.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.user.model.User;

@Entity
@Table(name = "event_ratings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Short rating;
}
