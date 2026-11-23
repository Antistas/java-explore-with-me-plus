package ru.practicum.gateway.compilation.dto;

import ru.practicum.gateway.event.dto.EventShortDto;

import java.util.List;

public record CompilationDto(Long id, List<EventShortDto> events, Boolean pinned, String title) {
}
