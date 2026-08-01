package ru.practicum.gateway.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.compilation.dto.CompilationDto;
import ru.practicum.gateway.compilation.dto.NewCompilationDto;
import ru.practicum.gateway.compilation.dto.UpdateCompilationRequest;
import ru.practicum.gateway.compilation.service.AdminCompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto request) {
        log.info("EWM: POST /admin/compilations | body={}", request);
        return compilationService.createCompilation(request);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable long compId,
            @RequestBody @Valid UpdateCompilationRequest request) {
        log.info("EWM: PATCH /admin/compilations/{} | body={}", compId, request);
        return compilationService.updateCompilation(compId, request);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("EWM: DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
    }
}
