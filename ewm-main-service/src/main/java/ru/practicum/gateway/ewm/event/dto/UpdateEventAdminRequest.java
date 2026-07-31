package ru.practicum.gateway.ewm.event.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateEventAdminRequest(
        @Size(min = 20, max = 2000, message = "Размер аннотации должен быть от 20 до 2000 символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Размер описания должен быть от 20 до 7000 символов")
        String description,

        String eventDate, // Должна быть дата в будущем формата "yyyy-MM-dd HH:mm:ss"

        LocationDto location,

        Boolean paid,

        @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
        Integer participantLimit,

        Boolean requestModeration,

        String stateAction, // PUBLISH_EVENT или REJECT_EVENT

        @Size(min = 3, max = 120, message = "Размер заголовка должен быть от 3 до 120 символов")
        String title
) {
}