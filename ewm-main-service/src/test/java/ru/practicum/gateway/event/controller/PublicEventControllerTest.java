package ru.practicum.gateway.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.event.service.PublicEventService;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.stats.client.StatsClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicEventController.class)
class PublicEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicEventService eventService;

    @MockBean
    private StatsClient statsClient;

    @Test
    void getEventsReturnsEmptyList() throws Exception {
        when(eventService.getEvents(isNull(), anyList(), isNull(), isNull(), isNull(),
                anyBoolean(), isNull(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/events")
                        .param("categories", "1", "2")
                        .param("onlyAvailable", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(statsClient).saveHit(eq("ewm-main-service"), eq("/events"), anyString());
    }

    @Test
    void getEventReturnsNotFoundError() throws Exception {
        when(eventService.getEvent(any(Long.class)))
                .thenThrow(new NotFoundException("Event with id=13 was not found"));

        mockMvc.perform(get("/events/{id}", 13))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found."))
                .andExpect(jsonPath("$.message").value("Event with id=13 was not found"))
                .andExpect(jsonPath("$.timestamp").isString());

        verify(statsClient).saveHit(eq("ewm-main-service"), eq("/events/13"), anyString());
    }
}
