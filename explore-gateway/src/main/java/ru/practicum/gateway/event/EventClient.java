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
import ru.practicum.gateway.event.dto.UpdateEventAdminRequest;
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
        addListQueryParam(uri, "categories", categories);

        return get(uri.build().toUriString());
    }

    public ResponseEntity<Object> getEvent(long eventId) {
        return get("/events/{eventId}", null, Map.of("eventId", eventId));
    }

    public ResponseEntity<Object> getEventsByUser(long userId, int from, int size) {
        return get(
                "/users/{userId}/events?from={from}&size={size}",
                userId,
                Map.of("userId", userId, "from", from, "size", size)
        );
    }

    public ResponseEntity<Object> addEvent(long userId, NewEventDto eventDto) {
        return post(
                "/users/{userId}/events",
                userId,
                Map.of("userId", userId),
                eventDto
        );
    }

    public ResponseEntity<Object> getEventByUser(long userId, long eventId) {
        return get(
                "/users/{userId}/events/{eventId}",
                userId,
                Map.of("userId", userId, "eventId", eventId)
        );
    }

    public ResponseEntity<Object> updateEventByUser(long userId,
                                                    long eventId,
                                                    UpdateEventUserRequest request) {
        return patch(
                "/users/{userId}/events/{eventId}",
                userId,
                Map.of("userId", userId, "eventId", eventId),
                request
        );
    }

    public ResponseEntity<Object> getRequestsForEvent(long userId, long eventId) {
        return get(
                "/users/{userId}/events/{eventId}/requests",
                userId,
                Map.of("userId", userId, "eventId", eventId)
        );
    }

    public ResponseEntity<Object> updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request) {
        return patch(
                "/users/{userId}/events/{eventId}/requests",
                userId,
                Map.of("userId", userId, "eventId", eventId),
                request
        );
    }

    public ResponseEntity<Object> searchEventsAdmin(List<Long> users,
                                                    List<String> states,
                                                    List<Long> categories,
                                                    String rangeStart,
                                                    String rangeEnd,
                                                    int from,
                                                    int size) {
        UriComponentsBuilder uri = UriComponentsBuilder
                .fromPath("/admin/events")
                .queryParam("from", from)
                .queryParam("size", size);

        addListQueryParam(uri, "users", users);
        addListQueryParam(uri, "states", states);
        addListQueryParam(uri, "categories", categories);
        addQueryParam(uri, "rangeStart", rangeStart);
        addQueryParam(uri, "rangeEnd", rangeEnd);

        return get(uri.build().toUriString());
    }

    public ResponseEntity<Object> updateEventAdmin(long eventId,
                                                   UpdateEventAdminRequest request) {
        return patch(
                "/admin/events/{eventId}",
                null,
                Map.of("eventId", eventId),
                request
        );
    }

    private void addQueryParam(UriComponentsBuilder uri, String name, Object value) {
        if (value != null) {
            uri.queryParam(name, value);
        }
    }

    private void addListQueryParam(UriComponentsBuilder uri, String name, List<?> values) {
        if (values != null && !values.isEmpty()) {
            uri.queryParam(name, values.toArray());
        }
    }
}
