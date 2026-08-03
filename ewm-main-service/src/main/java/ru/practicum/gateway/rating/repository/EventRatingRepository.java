package ru.practicum.gateway.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.gateway.rating.dto.EventRatingSum;
import ru.practicum.gateway.rating.model.EventRating;

import java.util.List;
import java.util.Optional;

public interface EventRatingRepository extends JpaRepository<EventRating, Long> {
    Optional<EventRating> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
            SELECT new ru.practicum.gateway.rating.dto.EventRatingSum(
                r.event.id, COALESCE(SUM(r.rating), 0)
            )
            FROM EventRating r
            WHERE r.event.id IN :eventIds
            GROUP BY r.event.id
            """)
    List<EventRatingSum> sumByEventIds(@Param("eventIds") List<Long> eventIds);
}
