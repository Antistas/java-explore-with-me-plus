package ru.practicum.gateway.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.category.model.Category;
import ru.practicum.gateway.category.repository.CategoryRepository;
import ru.practicum.gateway.event.dto.EventFullDto;
import ru.practicum.gateway.event.dto.UpdateEventAdminRequest;
import ru.practicum.gateway.event.mapper.EventMapper;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.event.model.EventState;
import ru.practicum.gateway.event.model.EventStateAction;
import ru.practicum.gateway.event.repository.EventRepository;
import ru.practicum.gateway.exception.BadRequestException;
import ru.practicum.gateway.exception.ConflictException;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.request.model.RequestStatus;
import ru.practicum.gateway.request.repository.ParticipationRequestRepository;
import ru.practicum.gateway.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    public List<EventFullDto> getEvents(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {
        List<EventState> eventStates = parseStates(states);
        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }

        Specification<Event> specification = Specification.where(null);
        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, builder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (!eventStates.isEmpty()) {
            specification = specification.and((root, query, builder) ->
                    root.get("state").in(eventStates));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, builder) ->
                    root.get("category").get("id").in(categories));
        }
        if (start != null) {
            specification = specification.and((root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.<LocalDateTime>get("eventDate"), start));
        }
        if (end != null) {
            specification = specification.and((root, query, builder) ->
                    builder.lessThanOrEqualTo(root.<LocalDateTime>get("eventDate"), end));
        }

        OffsetPageRequest pageable = new OffsetPageRequest(from, size, Sort.by("id"));
        return eventRepository.findAll(specification, pageable)
                .stream()
                .map(this::toFullDto)
                .toList();
    }

    @Transactional
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " was not found"));

        Category category = request.getCategory() == null
                ? null
                : categoryRepository.findById(request.getCategory())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id=" + request.getCategory() + " was not found"));

        EventMapper.updateEventFromAdminRequest(event, request, category);
        LocalDateTime now = LocalDateTime.now();
        if (request.getEventDate() != null && event.getEventDate().isBefore(now.plusHours(1))) {
            throw new ConflictException(
                    "Event date must be at least one hour after publication");
        }

        EventStateAction action = parseAction(request.getStateAction());
        if (action == EventStateAction.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException(
                        "Cannot publish the event because it is not in the right state: "
                                + event.getState());
            }
            if (event.getEventDate().isBefore(now.plusHours(1))) {
                throw new ConflictException(
                        "Event date must be at least one hour after publication");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(now);
        } else if (action == EventStateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot reject an already published event");
            }
            event.setState(EventState.CANCELED);
        }

        return toFullDto(eventRepository.save(event));
    }

    private EventFullDto toFullDto(Event event) {
        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(
                event.getId(), RequestStatus.CONFIRMED));
        return dto;
    }

    private List<EventState> parseStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return List.of();
        }
        try {
            return states.stream()
                    .map(EventState::valueOf)
                    .toList();
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown event state");
        }
    }

    private EventStateAction parseAction(String stateAction) {
        if (stateAction == null) {
            return null;
        }
        try {
            return EventStateAction.valueOf(stateAction);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown state action: " + stateAction);
        }
    }

    private LocalDateTime parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(
                    "Invalid date format: " + value + ". Expected yyyy-MM-dd HH:mm:ss");
        }
    }
}
