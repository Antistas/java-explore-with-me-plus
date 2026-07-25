package ru.practicum.gateway.ewm.exception;

public record ApiError(String status, String reason, String message, String timestamp) {
}
