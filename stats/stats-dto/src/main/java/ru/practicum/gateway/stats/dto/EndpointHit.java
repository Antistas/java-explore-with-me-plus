package ru.practicum.gateway.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}