package ru.practicum.gateway.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.gateway.event.dto.NewEventDto;
import ru.practicum.gateway.event.dto.UpdateEventAdminRequest;
import ru.practicum.gateway.event.dto.UpdateEventUserRequest;
import ru.practicum.gateway.stat.StatsClient;
import ru.practicum.gateway.stat.dto.EndpointHitDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@Validated
public class EventController {
    private static final DateTimeFormatter HIT_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventClient eventClient;
    private final StatsClient statsClient;
    private final String applicationName;

    public EventController(EventClient eventClient,
                           StatsClient statsClient,
                           @Value("${spring.application.name}") String applicationName) {
        this.eventClient = eventClient;
        this.statsClient = statsClient;
        this.applicationName = applicationName;
    }

    // 1. PUBLIC API (Публичный доступ)
    @GetMapping("/events")
    public ResponseEntity<Object> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            HttpServletRequest request) {

        log.info("Gateway stub: GET /events | text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        saveHit(request);
        return eventClient.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Object> getEventByIdPublic(@PathVariable Long id,
                                                     HttpServletRequest request) {
        log.info("Gateway stub: GET /events/{}", id);
        saveHit(request);
        return eventClient.getEvent(id);
    }

    private void saveHit(HttpServletRequest request) {
        EndpointHitDto hit = EndpointHitDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(HIT_TIMESTAMP_FORMATTER))
                .build();

        statsClient.saveHit(hit);
    }

    // 2. PRIVATE API (Для авторизованных пользователей)
    @GetMapping("/users/{userId}/events")
    public ResponseEntity<Object> getEventsByUserPrivate(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("GET /users/{}/events | from={}, size={}", userId, from, size);
        return eventClient.getEventsByUser(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<Object> addEventPrivate(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto eventDto) {

        log.info("POST /users/{}/events | body: {}", userId, eventDto);
        return eventClient.addEvent(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventByIdPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventClient.getEventByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEventPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {

        log.info("PATCH /users/{}/events/{} | body: {}", userId, eventId, updateRequest);
        return eventClient.updateEventByUser(userId, eventId, updateRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsForEventPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return eventClient.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> updateRequestStatusPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {

        log.info("PATCH /users/{}/events/{}/requests | body: {}", userId, eventId, statusUpdateRequest);
        return eventClient.updateRequestStatus(userId, eventId, statusUpdateRequest);
    }

    // 3. ADMIN API (Для администраторов)
    @GetMapping("/admin/events")
    public ResponseEntity<Object> searchEventsAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Gateway stub: GET /admin/events | users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/events/{eventId}")
    public ResponseEntity<Object> updateEventAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest adminUpdateRequest) {

        log.info("Gateway stub: PATCH /admin/events/{} | body: {}", eventId, adminUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
