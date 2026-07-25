package ru.practicum.gateway.compilation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.compilation.dto.NewCompilationDto;
import ru.practicum.gateway.compilation.dto.UpdateCompilationRequest;

@RestController
@Slf4j
@Validated
public class CompilationController {

    // 1. PUBLIC API (Доступно всем)

    @GetMapping("/compilations")
    public ResponseEntity<Object> getCompilationsPublic(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Gateway stub: GET /compilations | pinned={}, from={}, size={}", pinned, from, size);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilationByIdPublic(@PathVariable Long compId) {
        log.info("Gateway stub: GET /compilations/{}", compId);
        return ResponseEntity.ok().build();
    }

    // 2. ADMIN API (Только для администраторов)

    @PostMapping("/admin/compilations")
    public ResponseEntity<Object> addCompilationAdmin(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Gateway stub: POST /admin/compilations | body: {}", compilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilationAdmin(@PathVariable Long compId) {
        log.info("Gateway stub: DELETE /admin/compilations/{}", compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<Object> updateCompilationAdmin(
            @PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateRequest) {

        log.info("Gateway stub: PATCH /admin/compilations/{} | body: {}", compId, updateRequest);
        return ResponseEntity.ok().build();
    }
}