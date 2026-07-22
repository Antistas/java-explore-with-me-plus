package ru.practicum.ewm.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.client.StatsClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(PublicEventController.class)
@Import(PublicEventControllerTest.StatsClientTestConfiguration.class)
class PublicEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CapturingStatsClient statsClient;

    @Test
    void getEventsReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/events")
                        .param("categories", "1", "2")
                        .param("onlyAvailable", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        assertEquals("ewm-main-service", statsClient.app);
        assertEquals("/events", statsClient.uri);
        assertEquals("127.0.0.1", statsClient.ip);
    }

    @Test
    void getEventReturnsNotFoundError() throws Exception {
        mockMvc.perform(get("/events/{id}", 13))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found!!!"))
                .andExpect(jsonPath("$.message").value("Event with id=13 was not found"))
                .andExpect(jsonPath("$.timestamp").isString());

        assertEquals("ewm-main-service", statsClient.app);
        assertEquals("/events/13", statsClient.uri);
        assertEquals("127.0.0.1", statsClient.ip);
    }

    @TestConfiguration
    static class StatsClientTestConfiguration {
        @Bean
        CapturingStatsClient statsClient() {
            return new CapturingStatsClient();
        }
    }

    static class CapturingStatsClient extends StatsClient {
        private String app;
        private String uri;
        private String ip;

        CapturingStatsClient() {
            super("http://localhost");
        }

        @Override
        public void saveHit(String app, String uri, String ip) {
            this.app = app;
            this.uri = uri;
            this.ip = ip;
        }
    }
}
