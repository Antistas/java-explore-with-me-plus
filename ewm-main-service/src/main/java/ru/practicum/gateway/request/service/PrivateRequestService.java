package ru.practicum.gateway.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.model.EventState;
import ru.practicum.gateway.event.repository.EventRepository;
import ru.practicum.gateway.exception.BadRequestException;
import ru.practicum.gateway.exception.ConflictException;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.request.dto.ParticipationRequestDto;
import ru.practicum.gateway.request.mapper.RequestMapper;
import ru.practicum.gateway.request.model.ParticipationRequest;
import ru.practicum.gateway.request.model.RequestStatus;
import ru.practicum.gateway.request.repository.ParticipationRequestRepository;
import ru.practicum.gateway.user.model.User;
import ru.practicum.gateway.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Getting requests for user with id: {}", userId);

        checkUserExists(userId);

        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        log.info("Adding participation request: userId={}, eventId={}", userId, eventId);

        User user = checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot participate in own event");
        }

        if (requestRepository.existsByEventIdAndRequesterIdAndStatus(
                eventId, userId, RequestStatus.PENDING)) {
            throw new ConflictException("Duplicate request already exists");
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit has been reached");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest saved = requestRepository.save(request);
        log.info("Participation request created with id: {}", saved.getId());
        return RequestMapper.toParticipationRequestDto(saved);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Canceling request: userId={}, requestId={}", userId, requestId);

        checkUserExists(userId);

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

        if (request.getStatus() == RequestStatus.REJECTED || request.getStatus() == RequestStatus.CANCELED) {
            throw new BadRequestException("Request cannot be canceled because it is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest canceled = requestRepository.save(request);
        log.info("Request {} canceled", requestId);
        return RequestMapper.toParticipationRequestDto(canceled);
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Event checkEventExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }
}