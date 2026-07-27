package ru.practicum.gateway.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Размер аннотации должен быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Категория должна быть указана")
    private Long category;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Размер описания должен быть от 20 до 7000 символов")
    private String description;

    @NotBlank(message = "Дата события должна быть указана")
    private String eventDate;

    @NotNull(message = "Локация события должна быть указана")
    @Valid
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Размер заголовка должен быть от 3 до 120 символов")
    private String title;
}