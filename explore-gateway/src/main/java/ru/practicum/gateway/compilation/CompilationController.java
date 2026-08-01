package ru.practicum.gateway.compilation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationClient compilationClient;

    @GetMapping("/compilations")
    public ResponseEntity<Object> getCompilationsPublic(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Gateway: GET /compilations | pinned={}, from={}, size={}", pinned, from, size);
        return compilationClient.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilationByIdPublic(@PathVariable Long compId) {
        log.info("Gateway: GET /compilations/{}", compId);
        return compilationClient.getCompilation(compId);
    }

    @PostMapping("/admin/compilations")
    public ResponseEntity<Object> addCompilationAdmin(
            @RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Gateway: POST /admin/compilations | body={}", compilationDto);
        return compilationClient.createCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilationAdmin(@PathVariable Long compId) {
        log.info("Gateway: DELETE /admin/compilations/{}", compId);
        return compilationClient.deleteCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<Object> updateCompilationAdmin(
            @PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateRequest) {
        log.info("Gateway: PATCH /admin/compilations/{} | body={}", compId, updateRequest);
        return compilationClient.updateCompilation(compId, updateRequest);
    }
}
