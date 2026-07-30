package ru.practicum.gateway.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.ewm.category.model.Category;
import ru.practicum.gateway.ewm.category.repository.CategoryRepository;
import ru.practicum.gateway.ewm.event.dto.*;
import ru.practicum.gateway.ewm.event.mapper.EventMapper;
import ru.practicum.gateway.ewm.event.model.Event;
import ru.practicum.gateway.ewm.event.model.EventState;
import ru.practicum.gateway.ewm.event.repository.EventRepository;
import ru.practicum.gateway.ewm.exception.BadRequestException;
import ru.practicum.gateway.ewm.exception.ConflictException;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.gateway.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.gateway.ewm.request.mapper.RequestMapper;
import ru.practicum.gateway.ewm.request.model.ParticipationRequest;
import ru.practicum.gateway.ewm.request.model.RequestStatus;
import ru.practicum.gateway.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.gateway.ewm.user.model.User;
import ru.practicum.gateway.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    /**
     * Получение событий, добавленных текущим пользователем
     */
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.info("Getting events for user with id: {}", userId);

        checkUserExists(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return events.stream()
                .map(event -> {
                    long confirmedRequests = requestRepository.countByEventIdAndStatus(
                            event.getId(), RequestStatus.CONFIRMED);
                    EventShortDto dto = EventMapper.toEventShortDto(event);
                    dto.setConfirmedRequests(confirmedRequests);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Добавление нового события
     */
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Creating new event for user: {}", userId);

        User initiator = checkUserExists(userId);
        Category category = checkCategoryExists(newEventDto.getCategory());

        // Проверка: дата события не раньше чем через 2 часа
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Event event = EventMapper.toEntity(newEventDto, category, initiator);
        Event saved = eventRepository.save(event);

        log.info("Event created with id: {}", saved.getId());
        return EventMapper.toEventFullDto(saved);
    }

    /**
     * Получение полной информации о событии по его id
     */
    public EventFullDto getEventById(Long userId, Long eventId) {
        log.info("Getting event {} for user {}", eventId, userId);

        checkUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setConfirmedRequests(confirmedRequests);

        return dto;
    }

    /**
     * Изменение события добавленного текущим пользователем
     */
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Updating event {} for user {}", eventId, userId);

        checkUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        // Проверка: можно изменять только PENDING или CANCELED
        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        // Обработка stateAction
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new BadRequestException("Unknown state action: " + updateRequest.getStateAction());
            }
        }

        // Обновление категории
        Category category = null;
        if (updateRequest.getCategory() != null) {
            category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id " + updateRequest.getCategory() + " not found"));
        }

        // Обновление полей
        EventMapper.updateEventFromRequest(event, updateRequest, category);

        // Проверка даты
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Event updated = eventRepository.save(event);
        log.info("Event {} updated", eventId);

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        EventFullDto dto = EventMapper.toEventFullDto(updated);
        dto.setConfirmedRequests(confirmedRequests);

        return dto;
    }

    /**
     * Получение информации о запросах на участие в событии
     */
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        log.info("Getting participants for event {} by user {}", eventId, userId);

        checkUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии
     */
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest request) {

        log.info("Changing request status for event {} by user {}", eventId, userId);

        checkUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        // Проверка: только для событий с модерацией
        if (!event.getRequestModeration()) {
            throw new BadRequestException("Request moderation is disabled for this event");
        }

        // Проверка: лимит участников
        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit has been reached");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(request.getRequestIds());
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest participationRequest : requests) {
            // Проверка: статус должен быть PENDING
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }

            if (request.getStatus().equals("CONFIRMED")) {
                // Проверка лимита
                if (event.getParticipantLimit() != 0 &&
                        confirmedCount >= event.getParticipantLimit()) {
                    // Отклоняем все оставшиеся
                    participationRequest.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(participationRequest);
                } else {
                    participationRequest.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(participationRequest);
                    confirmedCount++;
                }
            } else if (request.getStatus().equals("REJECTED")) {
                participationRequest.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(participationRequest);
            } else {
                throw new BadRequestException("Unknown status: " + request.getStatus());
            }
        }

        requestRepository.saveAll(requests);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));
        result.setRejectedRequests(rejectedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));

        log.info("Request status changed for event {}", eventId);
        return result;
    }

    // ==================== PRIVATE METHODS ====================

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Category checkCategoryExists(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id " + categoryId + " not found"));
    }
}