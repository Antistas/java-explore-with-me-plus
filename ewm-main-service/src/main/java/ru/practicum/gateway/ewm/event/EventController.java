package ru.practicum.gateway.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.ewm.event.dto.EventFullDto;
import ru.practicum.gateway.ewm.event.dto.UpdateEventAdminRequest;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {

    private final EventService eventService;


    @GetMapping
    public ResponseEntity<List<EventFullDto>> searchEventsAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Main Service: GET /admin/events | users={}, states={}, categories={}, start={}, end={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> events = eventService.searchEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok(events);
    }


    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateRequest) {

        log.info("Main Service: PATCH /admin/events/{} | body: {}", eventId, updateRequest);
        EventFullDto updatedEvent = eventService.updateEventAdmin(eventId, updateRequest);
        return ResponseEntity.ok(updatedEvent);
    }
}