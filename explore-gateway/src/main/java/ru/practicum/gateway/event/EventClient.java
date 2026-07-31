package ru.practicum.gateway.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.gateway.event.dto.NewEventDto;
import ru.practicum.gateway.event.dto.UpdateEventUserRequest;

import java.util.List;
import java.util.Map;

@Service
public class EventClient extends BaseClient {

    public EventClient(@Value("${ewm-main-service.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> getEvents(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            String rangeStart,
                                            String rangeEnd,
                                            boolean onlyAvailable,
                                            String sort,
                                            int from,
                                            int size) {
        UriComponentsBuilder uri = UriComponentsBuilder
                .fromPath("/events")
                .queryParam("onlyAvailable", onlyAvailable)
                .queryParam("from", from)
                .queryParam("size", size);

        addQueryParam(uri, "text", text);
        addQueryParam(uri, "paid", paid);
        addQueryParam(uri, "rangeStart", rangeStart);
        addQueryParam(uri, "rangeEnd", rangeEnd);
        addQueryParam(uri, "sort", sort);

        if (categories != null && !categories.isEmpty()) {
            uri.queryParam("categories", categories.toArray());
        }

        return get(uri.build().toUriString());
    }

    public ResponseEntity<Object> getEvent(long eventId) {
        return get("/events/{eventId}", null, Map.of("eventId", eventId));
    }

    public ResponseEntity<Object> getEventsByUser(Long userId, int from, int size) {
        return get(
                "/users/{userId}/events?from={from}&size={size}",
                userId,
                Map.of("userId", userId, "from", from, "size", size)
        );
    }

    public ResponseEntity<Object> addEvent(Long userId, NewEventDto eventDto) {
        return post(
                "/users/{userId}/events",
                userId,
                Map.of("userId", userId),
                eventDto
        );
    }

    public ResponseEntity<Object> getEventByUser(Long userId, Long eventId) {
        return get(
                "/users/{userId}/events/{eventId}",
                userId,
                Map.of("userId", userId, "eventId", eventId)
        );
    }

    public ResponseEntity<Object> updateEventByUser(
            Long userId,
            Long eventId,
            UpdateEventUserRequest updateRequest
    ) {
        return patch(
                "/users/{userId}/events/{eventId}",
                userId,
                Map.of("userId", userId, "eventId", eventId),
                updateRequest
        );
    }

    public ResponseEntity<Object> getRequestsForEvent(Long userId, Long eventId) {
        return get(
                "/users/{userId}/events/{eventId}/requests",
                userId,
                Map.of("userId", userId, "eventId", eventId)
        );
    }

    public ResponseEntity<Object> updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest
    ) {
        return patch(
                "/users/{userId}/events/{eventId}/requests",
                userId,
                Map.of("userId", userId, "eventId", eventId),
                statusUpdateRequest
        );
    }

    private void addQueryParam(UriComponentsBuilder uri, String name, Object value) {
        if (value != null) {
            uri.queryParam(name, value);
        }
    }
}
