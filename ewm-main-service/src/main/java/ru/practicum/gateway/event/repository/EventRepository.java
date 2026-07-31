package ru.practicum.gateway.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.model.EventState;
import ru.practicum.gateway.request.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByState(EventState state);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = :state
              AND (
                  :textEmpty = TRUE
                  OR LOWER(e.annotation) LIKE CONCAT('%', :text, '%')
                  OR LOWER(e.description) LIKE CONCAT('%', :text, '%')
              )
              AND (
                  :categoriesEmpty = TRUE
                  OR e.category.id IN :categories
              )
              AND (:paidEmpty = TRUE OR e.paid = :paid)
              AND e.eventDate >= :start
              AND e.eventDate <= :end
              AND (
                  :onlyAvailable = FALSE
                  OR e.participantLimit = 0
                  OR (
                      SELECT COUNT(r)
                      FROM ParticipationRequest r
                      WHERE r.event = e
                        AND r.status = :confirmedStatus
                  ) < e.participantLimit
              )
            ORDER BY e.eventDate ASC, e.id ASC
            """)
    List<Event> findPublicEvents(
            @Param("state") EventState state,
            @Param("text") String text,
            @Param("textEmpty") boolean textEmpty,
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("paid") boolean paid,
            @Param("paidEmpty") boolean paidEmpty,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("onlyAvailable") boolean onlyAvailable,
            @Param("confirmedStatus") RequestStatus confirmedStatus,
            Pageable pageable
    );
}
