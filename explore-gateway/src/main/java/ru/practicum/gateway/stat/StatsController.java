package ru.practicum.gateway.stat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.stat.dto.EndpointHitDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsClient statsClient;


    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Gateway: Получен запрос POST /hit с телом: {}", hitDto);
        return statsClient.saveHit(hitDto);
    }


    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(
            @RequestParam @NotBlank(message = "Параметр start не должен быть пустым") String start,
            @RequestParam @NotBlank(message = "Параметр end не должен быть пустым") String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {

        log.info("Gateway: Получен запрос GET /stats (start={}, end={}, uris={}, unique={})",
                start, end, uris, unique);

        return statsClient.getStats(start, end, uris, unique);
    }
}