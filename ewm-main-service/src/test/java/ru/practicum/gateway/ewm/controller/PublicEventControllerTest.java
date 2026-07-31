package ru.practicum.gateway.ewm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.event.controller.PublicEventController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicEventController.class)
class PublicEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEventsReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/events")
                        .param("categories", "1", "2")
                        .param("onlyAvailable", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }

    @Test
    void getEventReturnsNotFoundError() throws Exception {
        mockMvc.perform(get("/events/{id}", 13))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.reason").value("The required object was not found!!!"))
                .andExpect(jsonPath("$.message").value("Event with id=13 was not found"))
                .andExpect(jsonPath("$.timestamp").isString());

    }
}
