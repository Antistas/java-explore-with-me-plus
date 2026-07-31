package ru.practicum.gateway.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.event.controller.PrivateEventController;
import ru.practicum.gateway.event.dto.*;
import ru.practicum.gateway.event.model.EventState;
import ru.practicum.gateway.event.service.PrivateEventService;
import ru.practicum.gateway.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.gateway.request.dto.ParticipationRequestDto;
import ru.practicum.gateway.request.model.RequestStatus;
import ru.practicum.gateway.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivateEventController.class)
class PrivateEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrivateEventService eventService;

    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;
    private NewEventDto newEventDto;
    private UpdateEventUserRequest updateRequest;
    private ParticipationRequestDto participationRequestDto;
    private EventRequestStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(1L)
                .name("Test User")
                .build();

        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Test Category")
                .build();

        LocationDto locationDto = LocationDto.builder()
                .lat(55.7558)
                .lon(37.6173)
                .build();

        eventFullDto = EventFullDto.builder()
                .id(1L)
                .annotation("Test annotation for the event")
                .category(categoryDto)
                .description("Test description for the event")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(userShortDto)
                .location(locationDto)
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Test Event")
                .createdOn(LocalDateTime.now())
                .confirmedRequests(0L)
                .views(0L)
                .build();

        eventShortDto = EventShortDto.builder()
                .id(1L)
                .annotation("Test annotation")
                .category(categoryDto)
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(userShortDto)
                .paid(false)
                .title("Test Event")
                .confirmedRequests(0L)
                .views(0L)
                .build();

        newEventDto = NewEventDto.builder()
                .annotation("Test annotation for new event")
                .category(1L)
                .description("Test description for new event")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location(locationDto)
                .paid(false)
                .participantLimit(5)
                .requestModeration(true)
                .title("New Test Event")
                .build();

        updateRequest = UpdateEventUserRequest.builder()
                .annotation("Updated annotation")
                .description("Updated description")
                .title("Updated Title")
                .stateAction("SEND_TO_REVIEW")
                .build();

        participationRequestDto = ParticipationRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .event(1L)
                .requester(2L)
                .status(RequestStatus.valueOf(RequestStatus.PENDING.name()))
                .build();

        statusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status("CONFIRMED")
                .build();
    }

    @Test
    void getEvents_ShouldReturnListOfEvents() throws Exception {
        List<EventShortDto> events = List.of(eventShortDto);
        when(eventService.getEvents(anyLong(), anyInt(), anyInt())).thenReturn(events);

        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].annotation").value("Test annotation"))
                .andExpect(jsonPath("$[0].title").value("Test Event"));

        verify(eventService, times(1)).getEvents(eq(1L), eq(0), eq(10));
    }

    @Test
    void getEvents_WithDefaultParams_ShouldReturnList() throws Exception {
        List<EventShortDto> events = List.of(eventShortDto);
        when(eventService.getEvents(anyLong(), anyInt(), anyInt())).thenReturn(events);

        mockMvc.perform(get("/users/1/events"))
                .andExpect(status().isOk());

        verify(eventService, times(1)).getEvents(eq(1L), eq(0), eq(10));
    }

    @Test
    void getEvents_WithNoEvents_ShouldReturnEmptyList() throws Exception {
        when(eventService.getEvents(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/users/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService, times(1)).getEvents(eq(1L), eq(0), eq(10));
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() throws Exception {
        when(eventService.createEvent(anyLong(), any(NewEventDto.class))).thenReturn(eventFullDto);

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.annotation").value("Test annotation for the event"))
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.state").value("PENDING"));

        verify(eventService, times(1)).createEvent(eq(1L), any(NewEventDto.class));
    }

    @Test
    void createEvent_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        NewEventDto invalidDto = NewEventDto.builder()
                .annotation("")
                .category(null)
                .build();

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(anyLong(), any(NewEventDto.class));
    }

    @Test
    void createEvent_WithShortAnnotation_ShouldReturnBadRequest() throws Exception {
        NewEventDto invalidDto = NewEventDto.builder()
                .annotation("Short")
                .category(1L)
                .description("Valid description for testing")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location(LocationDto.builder().lat(55.7558).lon(37.6173).build())
                .title("Valid Title")
                .build();

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(anyLong(), any(NewEventDto.class));
    }

    @Test
    void getEventById_ShouldReturnEvent() throws Exception {
        when(eventService.getEventById(anyLong(), anyLong())).thenReturn(eventFullDto);

        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.annotation").value("Test annotation for the event"))
                .andExpect(jsonPath("$.title").value("Test Event"));

        verify(eventService, times(1)).getEventById(eq(1L), eq(1L));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() throws Exception {
        EventFullDto updatedEvent = EventFullDto.builder()
                .id(1L)
                .annotation("Updated annotation")
                .description("Updated description")
                .title("Updated Title")
                .state(EventState.PENDING)
                .build();

        when(eventService.updateEvent(anyLong(), anyLong(), any(UpdateEventUserRequest.class)))
                .thenReturn(updatedEvent);

        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annotation").value("Updated annotation"))
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(eventService, times(1)).updateEvent(eq(1L), eq(1L), any(UpdateEventUserRequest.class));
    }

    @Test
    void getEventParticipants_ShouldReturnListOfRequests() throws Exception {
        List<ParticipationRequestDto> requests = List.of(participationRequestDto);
        when(eventService.getEventParticipants(anyLong(), anyLong())).thenReturn(requests);

        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].event").value(1L))
                .andExpect(jsonPath("$[0].requester").value(2L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(eventService, times(1)).getEventParticipants(eq(1L), eq(1L));
    }

    @Test
    void getEventParticipants_WithNoRequests_ShouldReturnEmptyList() throws Exception {
        when(eventService.getEventParticipants(anyLong(), anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService, times(1)).getEventParticipants(eq(1L), eq(1L));
    }

    @Test
    void changeRequestStatus_ShouldReturnUpdatedRequests() throws Exception {
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(List.of(participationRequestDto))
                .rejectedRequests(List.of())
                .build();

        when(eventService.changeRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(result);

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(1L))
                .andExpect(jsonPath("$.confirmedRequests[0].status").value("PENDING"));

        verify(eventService, times(1)).changeRequestStatus(eq(1L), eq(1L), any(EventRequestStatusUpdateRequest.class));
    }
}