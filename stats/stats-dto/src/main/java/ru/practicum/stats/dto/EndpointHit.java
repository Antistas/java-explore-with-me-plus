package ru.practicum.stats.dto;

public record EndpointHit(String app, String uri, String ip, String timestamp) {
}
