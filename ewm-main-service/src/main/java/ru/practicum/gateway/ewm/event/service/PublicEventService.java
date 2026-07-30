package ru.practicum.gateway.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.gateway.ewm.event.dto.CategoryDto;
import ru.practicum.gateway.ewm.event.dto.EventFullDto;
import ru.practicum.gateway.ewm.event.dto.EventShortDto;
import ru.practicum.gateway.ewm.event.dto.LocationDto;
import ru.practicum.gateway.ewm.event.dto.UserShortDto;
import ru.practicum.gateway.ewm.exception.BadRequestException;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.ewm.model.Event;
import ru.practicum.gateway.ewm.model.EventState;
import ru.practicum.gateway.ewm.model.RequestStatus;
import ru.practicum.gateway.ewm.repository.EventRepository;
import ru.practicum.gateway.ewm.util.OffsetPageRequest;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicEventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime STATS_BEGIN = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    public enum EventSort {
        EVENT_DATE,
        VIEWS
    }

    private static final String PUBLIC_APP = "explore-gateway";

    public List<EventShortDto> getEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size) {
        validatePage(from, size);
        LocalDateTime start = parseDate(rangeStart, false);
        LocalDateTime end = parseDate(rangeEnd, true);
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end != null && start.isAfter(end)) {
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }
        if (end == null) {
            end = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        }

        EventSort eventSort;
        try {
            eventSort = sort == null ? EventSort.EVENT_DATE : EventSort.valueOf(sort);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown sort value: " + sort);
        }

        boolean textEmpty = text == null || text.isBlank();
        String normalizedText = textEmpty ? "" : text.toLowerCase(Locale.ROOT);
        boolean categoriesEmpty = categories == null || categories.isEmpty();
        List<Long> categoryIds = categoriesEmpty  ? List.of(-1L) : categories;
        boolean paidEmpty = paid == null;
        boolean paidValue = Boolean.TRUE.equals(paid);

        Pageable pageable = eventSort == EventSort.EVENT_DATE
                ? new OffsetPageRequest(from, size, Sort.unsorted())
                : Pageable.unpaged();

        List<Event> events = eventRepository.findPublicEvents(normalizedText, textEmpty, categoryIds, categoriesEmpty,
                paidValue, paidEmpty, start, end, onlyAvailable, pageable);

        Map<Long, Long> views = loadViewsSafely(events);
        if (eventSort == EventSort.VIEWS) {
            events = events.stream()
                    .sorted(
                            Comparator.<Event>comparingLong(event -> views.getOrDefault(event.getId(), 0L)
                            ).reversed().thenComparing(Event::getId)
                    )
                    .toList();
            events = page(events, from, size);
        }

        return events.stream()
                .map(event -> toShortDto(event, views.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    public EventFullDto getEvent(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " was not found"));

        long views = loadViewsSafely(List.of(event)).getOrDefault(eventId, 0L);
        return toFullDto(event, views);
    }

    private Map<Long, Long> loadViewsSafely(List<Event> events) {
        try {
            return loadViews(events);
        } catch (RestClientException exception) {
            log.warn("Stats-server is unavailable", exception);
            return Map.of();
        }
    }

    private Map<Long, Long> loadViews(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }
        List<String> uris = events.stream().map(event -> "/events/" + event.getId()).toList();
        List<ViewStats> stats = statsClient.getStats(STATS_BEGIN, LocalDateTime.now().plusSeconds(1), uris, true);
        Map<Long, Long> result = new HashMap<>();

        for (ViewStats stat : stats) {
            if (!PUBLIC_APP.equals(stat.getApp())) {
                continue;
            }
            String prefix = "/events/";
            if (stat.getUri() != null && stat.getUri().startsWith(prefix)) {
                try {
                    result.put(Long.parseLong(stat.getUri().substring(prefix.length())), stat.getHits());
                } catch (NumberFormatException ignored) {
                    // Statistics for unrelated URIs are ignored.
                }
            }
        }
        return result;
    }

    public List<EventShortDto> toShortDtos(List<Event> events) {
        Map<Long, Long> views = loadViewsSafely(events);
        return events.stream()
                .map(event -> toShortDto(event, views.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    private EventShortDto toShortDto(Event event, long views) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                confirmedRequests(event),
                format(event.getEventDate()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                views
        );
    }

    private EventFullDto toFullDto(Event event, long views) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                confirmedRequests(event),
                format(event.getCreatedOn()),
                event.getDescription(),
                format(event.getEventDate()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                new LocationDto(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                format(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                views
        );
    }

    private long confirmedRequests(Event event) {
        return event.getRequests().stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .count();
    }

    private LocalDateTime parseDate(String value, boolean endOfDay) {
        if (value == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException exception) {
            try {
                LocalDate date = LocalDate.parse(value);
                return endOfDay ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
            } catch (DateTimeParseException ignored) {
                throw new BadRequestException("Invalid date format: " + value +
                        ". Expected yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private String format(LocalDateTime value) {
        return value == null ? null : value.format(FORMATTER);
    }

    private void validatePage(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException("from must be >= 0 and size must be > 0");
        }
    }

    private <T> List<T> page(List<T> values, int from, int size) {
        if (from >= values.size()) {
            return List.of();
        }
        return values.subList(from, Math.min(from + size, values.size()));
    }
}
