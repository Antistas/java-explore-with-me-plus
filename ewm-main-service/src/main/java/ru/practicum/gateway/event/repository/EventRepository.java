package ru.practicum.gateway.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.model.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);
    List<Event> findAllByState(EventState state);
}