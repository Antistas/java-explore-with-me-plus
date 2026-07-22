package ru.practicum.ewm.exception;

public record ApiError(String status, String reason, String message, String timestamp) {
}
