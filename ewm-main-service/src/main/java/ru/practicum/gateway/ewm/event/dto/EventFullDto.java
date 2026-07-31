package ru.practicum.gateway.ewm.event.dto;

import ru.practicum.gateway.ewm.category.dto.CategoryDto;

public record EventFullDto(
        Long id,
        String annotation,
        CategoryDto category,
        Long confirmedRequests,
        String createdOn,
        String description,
        String eventDate,
        UserShortDto initiator,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        String publishedOn,
        Boolean requestModeration,
        String state,
        String title,
        Long views) {
}
