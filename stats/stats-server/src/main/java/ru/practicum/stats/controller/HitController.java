package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.StatsService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HitController {
    private static final Logger log = LoggerFactory.getLogger(HitController.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody EndpointHit hit) {
        log.info("Received endpoint hit: {}", hit);
        statsService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        LocalDateTime startDateTime = parseStart(start);
        LocalDateTime endDateTime = parseEnd(end);

        log.info("Get stats: start={}, end={}, uris={}, unique={}",
                startDateTime, endDateTime, uris, unique);
        return statsService.getStats(startDateTime, endDateTime, uris, unique);
    }

    private LocalDateTime parseStart(String value) {
        if (value.length() == 10) {
            return LocalDate.parse(value).atStartOfDay();
        }
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    private LocalDateTime parseEnd(String value) {
        if (value.length() == 10) {
            return LocalDate.parse(value).atTime(LocalTime.MAX);
        }
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }
}
