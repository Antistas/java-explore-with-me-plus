package ru.practicum.gateway.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.gateway.stats.client.StatsClient;

@Configuration
public class StatsClientConfiguration {

    @Bean
    public StatsClient statsClient(@Value("${stats-server.url}") String statsServerUrl) {
        return new StatsClient(statsServerUrl);
    }
}
