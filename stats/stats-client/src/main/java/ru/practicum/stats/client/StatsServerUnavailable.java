package ru.practicum.stats.client;

/**
 * Адрес сервера статистики не удалось получить от службы обнаружения.
 */
public class StatsServerUnavailable extends RuntimeException {
    public StatsServerUnavailable(String message, Throwable cause) {
        super(message, cause);
    }
}
