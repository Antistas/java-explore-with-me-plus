package ru.practicum.gateway.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateCompilationRequest(
        Set<Long> events,
        Boolean pinned,
        @Size(min = 1, max = 50)
        String title) {
}
