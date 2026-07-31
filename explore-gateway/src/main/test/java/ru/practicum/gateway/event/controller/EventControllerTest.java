package ru.practicum.gateway.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.event.EventClient;
import ru.practicum.gateway.event.EventController;
import ru.practicum.gateway.event.dto.*;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHit;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventClient eventClient;

    @MockBean
    private StatsClient statsClient;

    private NewEventDto newEventDto;
    private UpdateEventUserRequest updateRequest;
    private EventRequestStatusUpdateRequest statusUpdateRequest;
    private UpdateEventAdminRequest adminUpdateRequest;

    @BeforeEach
    void setUp() {
        LocationDto locationDto = LocationDto.builder()
                .lat(55.7558F)
                .lon(37.6173F)
                .build();

        newEventDto = NewEventDto.builder()
                .annotation("Test annotation for new event")
                .category(1L)
                .description("Test description for new event")
                .eventDate(String.valueOf(LocalDateTime.now().plusDays(2)))
                .location(locationDto)
                .paid(false)
                .participantLimit(5)
                .requestModeration(true)
                .title("New Test Event")
                .build();

        updateRequest = UpdateEventUserRequest.builder()
                .annotation("Updated annotation for test")
                .description("Updated description for test")
                .title("Updated Title")
                .stateAction("SEND_TO_REVIEW")
                .build();

        statusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status("CONFIRMED")
                .build();

        adminUpdateRequest = UpdateEventAdminRequest.builder()
                .annotation("Admin updated annotation")
                .title("Admin Updated Title")
                .stateAction("PUBLISH_EVENT")
                .build();
    }

    @Test
    void getEventsPublic_ShouldReturnEvents() throws Exception {
        when(eventClient.getEvents(any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("categories", "1", "2")
                        .param("paid", "true")
                        .param("onlyAvailable", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEvents(
                eq("test"), eq(List.of(1L, 2L)), eq(true), isNull(), isNull(),
                eq(true), isNull(), eq(0), eq(10));
        verify(statsClient, times(1)).saveHit(any(EndpointHit.class));
    }

    @Test
    void getEventsPublic_WithInvalidFrom_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/events")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());

        verify(eventClient, never()).getEvents(any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt());
        verify(statsClient, never()).saveHit(any(EndpointHit.class));
    }

    @Test
    void getEventsPublic_WithInvalidSize_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/events")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(eventClient, never()).getEvents(any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt());
        verify(statsClient, never()).saveHit(any(EndpointHit.class));
    }

    @Test
    void getEventByIdPublic_ShouldReturnEvent() throws Exception {
        when(eventClient.getEvent(anyLong())).thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEvent(1L);
        verify(statsClient, times(1)).saveHit(any(EndpointHit.class));
    }

    @Test
    void getEventsByUserPrivate_ShouldReturnEvents() throws Exception {
        when(eventClient.getEventsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEventsByUser(1L, 0, 10);
    }

    @Test
    void getEventsByUserPrivate_WithInvalidUserId_ShouldReturnIsOk() throws Exception {
        mockMvc.perform(get("/users/0/events"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEventsByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void addEventPrivate_ShouldReturnCreated() throws Exception {
        when(eventClient.addEvent(anyLong(), any(NewEventDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("{}"));

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated());

        verify(eventClient, times(1)).addEvent(eq(1L), any(NewEventDto.class));
    }

    @Test
    void addEventPrivate_WithInvalidUserId_ShouldReturnIsOk() throws Exception {
        NewEventDto validDto = newEventDto;

        mockMvc.perform(post("/users/0/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).addEvent(anyLong(), any(NewEventDto.class));
    }

    @Test
    void addEventPrivate_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        NewEventDto invalidDto = NewEventDto.builder()
                .annotation("")
                .category(null)
                .build();

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(eventClient, never()).addEvent(anyLong(), any(NewEventDto.class));
    }

    @Test
    void getEventByIdPrivate_ShouldReturnEvent() throws Exception {
        when(eventClient.getEventByUser(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEventByUser(1L, 1L);
    }

    @Test
    void getEventByIdPrivate_WithInvalidUserId_ShouldReturnIsOk() throws Exception {
        mockMvc.perform(get("/users/0/events/1"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getEventByUser(anyLong(), anyLong());
    }

    @Test
    void updateEventPrivate_ShouldReturnUpdated() throws Exception {
        when(eventClient.updateEventByUser(anyLong(), anyLong(), any(UpdateEventUserRequest.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).updateEventByUser(eq(1L), eq(1L), any(UpdateEventUserRequest.class));
    }

    @Test
    void updateEventPrivate_WithInvalidUserId_ShouldReturnOk() throws Exception {
        UpdateEventUserRequest validRequest = updateRequest;
        when(eventClient.updateEventByUser(anyLong(), anyLong(), any(UpdateEventUserRequest.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(patch("/users/0/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).updateEventByUser(eq(0L), eq(1L), any(UpdateEventUserRequest.class));
    }

    @Test
    void getRequestsForEventPrivate_ShouldReturnRequests() throws Exception {
        when(eventClient.getRequestsForEvent(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).getRequestsForEvent(1L, 1L);
    }

    @Test
    void updateRequestStatusPrivate_ShouldReturnUpdated() throws Exception {
        when(eventClient.updateRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).updateRequestStatus(eq(1L), eq(1L), any(EventRequestStatusUpdateRequest.class));
    }

    @Test
    void updateRequestStatusPrivate_WithInvalidUserId_ShouldReturnOk() throws Exception {
        EventRequestStatusUpdateRequest validRequest = statusUpdateRequest;
        when(eventClient.updateRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(patch("/users/0/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(eventClient, times(1)).updateRequestStatus(eq(0L), eq(1L), any(EventRequestStatusUpdateRequest.class));
    }

    @Test
    void updateRequestStatusPrivate_WithEmptyRequestIds_ShouldReturnBadRequest() throws Exception {
        EventRequestStatusUpdateRequest invalidRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of())
                .status("CONFIRMED")
                .build();

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(eventClient, never()).updateRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class));
    }

    @Test
    void updateRequestStatusPrivate_WithNullStatus_ShouldReturnBadRequest() throws Exception {
        EventRequestStatusUpdateRequest invalidRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status(null)
                .build();

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(eventClient, never()).updateRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class));
    }

    @Test
    void searchEventsAdmin_ShouldReturnEvents() throws Exception {
        mockMvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PENDING", "PUBLISHED")
                        .param("categories", "1")
                        .param("rangeStart", "2026-01-01 00:00:00")
                        .param("rangeEnd", "2026-12-31 23:59:59")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void searchEventsAdmin_WithInvalidFrom_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/admin/events")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEventAdmin_ShouldReturnUpdated() throws Exception {
        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUpdateRequest)))
                .andExpect(status().isOk());
    }
}