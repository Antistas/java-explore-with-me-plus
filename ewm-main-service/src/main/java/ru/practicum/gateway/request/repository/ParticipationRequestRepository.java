package ru.practicum.gateway.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.request.model.ParticipationRequest;
import ru.practicum.gateway.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long userId);
    List<ParticipationRequest> findAllByEventId(Long eventId);
    Optional<ParticipationRequest> findByIdAndRequesterId(Long requestId, Long userId);
    boolean existsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, RequestStatus status);
    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}