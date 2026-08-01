package ru.practicum.stats.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.service.StatsService;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HitController.class)
class HitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    @Test
    void getStats_WhenStartIsAfterEnd_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2035-05-05 00:00:00")
                        .param("end", "2020-05-05 00:00:00")
                        .param("uris", "/events"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statsService);
    }
}
