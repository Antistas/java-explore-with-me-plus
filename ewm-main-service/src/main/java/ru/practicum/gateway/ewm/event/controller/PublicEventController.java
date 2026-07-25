package ru.practicum.gateway.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.ewm.event.dto.EventFullDto;
import ru.practicum.gateway.ewm.event.dto.EventShortDto;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.stats.client.StatsClient;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicEventController {
    private final StatsClient statsClient;
    private final String applicationName;

    public PublicEventController(StatsClient statsClient,
                                 @Value("${spring.application.name}") String applicationName) {
        this.statsClient = statsClient;
        this.applicationName = applicationName;
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        saveHit(request);
        return List.of();
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        saveHit(request);
        throw new NotFoundException("Event with id=" + id + " was not found");
    }

    private void saveHit(HttpServletRequest request) {
        statsClient.saveHit(applicationName, request.getRequestURI(), request.getRemoteAddr());
    }
}
