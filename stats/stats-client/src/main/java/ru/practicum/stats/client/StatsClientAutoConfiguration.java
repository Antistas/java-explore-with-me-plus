package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StatsClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StatsClient statsClient(@Value("${stats-server.url}") String statsServerUrl) {
        return new StatsClient(statsServerUrl);
    }
}
