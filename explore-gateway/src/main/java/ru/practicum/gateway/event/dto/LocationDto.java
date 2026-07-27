package ru.practicum.gateway.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull(message = "Широта (lat) должна быть указана")
    private Float lat;

    @NotNull(message = "Долгота (lon) должна быть указана")
    private Float lon;
}