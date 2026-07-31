package ru.practicum.gateway.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.gateway.ewm.model.ParticipationRequest;
import ru.practicum.gateway.ewm.model.RequestStatus;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {


    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}