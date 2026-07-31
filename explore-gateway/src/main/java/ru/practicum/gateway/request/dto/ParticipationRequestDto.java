package ru.practicum.gateway.request.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;

    @NotNull(message = "Event ID cannot be null")
    @Positive(message = "Event ID must be positive")
    private Long event;

    @NotNull(message = "Requester ID cannot be null")
    @Positive(message = "Requester ID must be positive")
    private Long requester;

    @NotNull(message = "Status cannot be null")
    private String status; // PENDING, CONFIRMED, REJECTED, CANCELED
}