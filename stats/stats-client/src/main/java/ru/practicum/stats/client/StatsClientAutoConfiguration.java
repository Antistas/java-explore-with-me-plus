package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@PropertySource("classpath:application.properties")
public class StatsClientAutoConfiguration {

    @Bean
    public StatsClient statsClient(@Value("${stats-server.url}") String statsServerUrl) {
        return new StatsClient(statsServerUrl);
    }
}
