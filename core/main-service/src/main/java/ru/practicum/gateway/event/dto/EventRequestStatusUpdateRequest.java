package ru.practicum.gateway.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotEmpty(message = "Request IDs cannot be empty")
    private List<Long> requestIds;

    @NotNull(message = "Status cannot be null")
    private String status;
}