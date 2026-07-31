package ru.practicum.gateway.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.request.RequestClient;
import ru.practicum.gateway.request.RequestController;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @BeforeEach
    void setUp() {
        // Настройка моков не требуется для простых тестов
    }


    @Test
    void getUserRequestsPrivate_WithValidUserId_ShouldReturnOk() throws Exception {
        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getUserRequests(1L);
    }

    @Test
    void getUserRequestsPrivate_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/users/0/requests"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getUserRequests(anyLong());
    }

    @Test
    void getUserRequestsPrivate_WithNegativeUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users/-1/requests"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getUserRequests(anyLong());
    }

    @Test
    void addParticipationRequestPrivate_WithValidParams_ShouldReturnCreated() throws Exception {
        when(requestClient.addParticipationRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated());

        verify(requestClient, times(1)).addParticipationRequest(1L, 1L);
    }

    @Test
    void addParticipationRequestPrivate_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users/0/requests")
                        .param("eventId", "1"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addParticipationRequest(anyLong(), anyLong());
    }

    @Test
    void addParticipationRequestPrivate_WithInvalidEventId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "0"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addParticipationRequest(anyLong(), anyLong());
    }

    @Test
    void addParticipationRequestPrivate_WithNegativeEventId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "-1"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addParticipationRequest(anyLong(), anyLong());
    }

    @Test
    void addParticipationRequestPrivate_WithoutEventId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users/1/requests"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addParticipationRequest(anyLong(), anyLong());
    }

    @Test
    void cancelRequestPrivate_WithValidParams_ShouldReturnOk() throws Exception {
        when(requestClient.cancelRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).cancelRequest(1L, 1L);
    }

    @Test
    void cancelRequestPrivate_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/0/requests/1/cancel"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).cancelRequest(anyLong(), anyLong());
    }

    @Test
    void cancelRequestPrivate_WithInvalidRequestId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/1/requests/0/cancel"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).cancelRequest(anyLong(), anyLong());
    }

    @Test
    void cancelRequestPrivate_WithNegativeUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/-1/requests/1/cancel"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).cancelRequest(anyLong(), anyLong());
    }

    @Test
    void cancelRequestPrivate_WithNegativeRequestId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/1/requests/-1/cancel"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).cancelRequest(anyLong(), anyLong());
    }

    @Test
    void getUserRequestsPrivate_WithNonExistentUserId_ShouldPassToClient() throws Exception {
        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/999/requests"))
                .andExpect(status().isNotFound());

        verify(requestClient, times(1)).getUserRequests(999L);
    }

    @Test
    void addParticipationRequestPrivate_WithNonExistentEventId_ShouldPassToClient() throws Exception {
        when(requestClient.addParticipationRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "999"))
                .andExpect(status().isNotFound());

        verify(requestClient, times(1)).addParticipationRequest(1L, 999L);
    }

    @Test
    void cancelRequestPrivate_WithNonExistentRequestId_ShouldPassToClient() throws Exception {
        when(requestClient.cancelRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/users/1/requests/999/cancel"))
                .andExpect(status().isNotFound());

        verify(requestClient, times(1)).cancelRequest(1L, 999L);
    }

    @Test
    void getUserRequestsPrivate_ShouldIncludeXSharerUserIdHeader() throws Exception {
        when(requestClient.getUserRequests(anyLong()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/users/1/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getUserRequests(1L);
    }
}