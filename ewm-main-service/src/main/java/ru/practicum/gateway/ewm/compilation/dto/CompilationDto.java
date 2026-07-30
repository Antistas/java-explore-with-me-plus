package ru.practicum.gateway.ewm.compilation.dto;

import ru.practicum.gateway.ewm.event.dto.EventShortDto;

import java.util.List;

public record CompilationDto(Long id, List<EventShortDto> events, Boolean pinned, String title) {
}
