package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StatsClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StatsClient statsClient(DiscoveryClient discoveryClient,
                                   @Value("${stats-server.service-id:stats-server}") String statsServiceId) {
        return new StatsClient(discoveryClient, statsServiceId);
    }
}
