package ru.practicum.gateway.ewm.event.dto;

import ru.practicum.gateway.ewm.category.dto.CategoryDto;

public record EventShortDto(
        Long id,
        String annotation,
        CategoryDto category,
        Long confirmedRequests,
        String eventDate,
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views) {
}
