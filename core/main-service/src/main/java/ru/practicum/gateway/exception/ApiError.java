package ru.practicum.gateway.exception;

public record ApiError(String status, String reason, String message, String timestamp) {
}
