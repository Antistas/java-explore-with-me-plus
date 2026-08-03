package ru.practicum.gateway.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.event.EventState;
import ru.practicum.gateway.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private String description;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Long confirmedRequests;
    private Long views;
    private Long rating;
}
