package ru.practicum.gateway.ewm.event.dto;

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
