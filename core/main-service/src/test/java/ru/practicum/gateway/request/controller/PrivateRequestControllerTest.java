package ru.practicum.gateway.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.request.controller.PrivateRequestController;
import ru.practicum.gateway.request.dto.ParticipationRequestDto;
import ru.practicum.gateway.request.model.RequestStatus;
import ru.practicum.gateway.request.service.PrivateRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrivateRequestController.class)
class PrivateRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrivateRequestService requestService;

    private ParticipationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = ParticipationRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .event(1L)
                .requester(2L)
                .status(RequestStatus.valueOf(RequestStatus.PENDING.name()))
                .build();
    }

    @Test
    void getUserRequests_ShouldReturnListOfRequests() throws Exception {
        List<ParticipationRequestDto> requests = List.of(requestDto);
        when(requestService.getUserRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].event").value(1L))
                .andExpect(jsonPath("$[0].requester").value(2L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(requestService, times(1)).getUserRequests(eq(1L));
    }

    @Test
    void getUserRequests_WithNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(requestService, times(1)).getUserRequests(eq(1L));
    }

    @Test
    void getUserRequests_WithMultipleRequests_ShouldReturnAll() throws Exception {
        ParticipationRequestDto request2 = ParticipationRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .event(2L)
                .requester(2L)
                .status(RequestStatus.valueOf(RequestStatus.CONFIRMED.name()))
                .build();

        List<ParticipationRequestDto> requests = List.of(requestDto, request2);
        when(requestService.getUserRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));

        verify(requestService, times(1)).getUserRequests(eq(1L));
    }

    @Test
    void addParticipationRequest_ShouldReturnCreatedRequest() throws Exception {
        when(requestService.addParticipationRequest(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.event").value(1L))
                .andExpect(jsonPath("$.requester").value(2L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(requestService, times(1)).addParticipationRequest(eq(1L), eq(1L));
    }

    @Test
    void addParticipationRequest_WithDifferentUser_ShouldUseCorrectUser() throws Exception {
        ParticipationRequestDto requestForUser3 = ParticipationRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .event(1L)
                .requester(3L)
                .status(RequestStatus.valueOf(RequestStatus.PENDING.name()))
                .build();

        when(requestService.addParticipationRequest(eq(3L), anyLong())).thenReturn(requestForUser3);

        mockMvc.perform(post("/users/3/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requester").value(3L));

        verify(requestService, times(1)).addParticipationRequest(eq(3L), eq(1L));
        verify(requestService, never()).addParticipationRequest(eq(1L), anyLong());
    }

    @Test
    void addParticipationRequest_WithConfirmedStatus_ShouldReturnConfirmed() throws Exception {
        ParticipationRequestDto confirmedRequest = ParticipationRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .event(1L)
                .requester(2L)
                .status(RequestStatus.valueOf(RequestStatus.CONFIRMED.name()))
                .build();

        when(requestService.addParticipationRequest(anyLong(), anyLong())).thenReturn(confirmedRequest);

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(requestService, times(1)).addParticipationRequest(eq(1L), eq(1L));
    }

    @Test
    void cancelRequest_ShouldReturnCanceledRequest() throws Exception {
        ParticipationRequestDto canceledRequest = ParticipationRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .event(1L)
                .requester(2L)
                .status(RequestStatus.valueOf(RequestStatus.CANCELED.name()))
                .build();

        when(requestService.cancelRequest(anyLong(), anyLong())).thenReturn(canceledRequest);

        mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        verify(requestService, times(1)).cancelRequest(eq(1L), eq(1L));
    }

    @Test
    void cancelRequest_WithDifferentUser_ShouldUseCorrectIds() throws Exception {
        ParticipationRequestDto canceledRequest = ParticipationRequestDto.builder()
                .id(5L)
                .created(LocalDateTime.now())
                .event(2L)
                .requester(3L)
                .status(RequestStatus.valueOf(RequestStatus.CANCELED.name()))
                .build();

        when(requestService.cancelRequest(eq(3L), eq(5L))).thenReturn(canceledRequest);

        mockMvc.perform(patch("/users/3/requests/5/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.requester").value(3L));

        verify(requestService, times(1)).cancelRequest(eq(3L), eq(5L));
        verify(requestService, never()).cancelRequest(eq(1L), eq(5L));
    }

    @Test
    void cancelRequest_WithPendingRequest_ShouldCancelSuccessfully() throws Exception {
        when(requestService.cancelRequest(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            Long requestId = invocation.getArgument(1);
            return ParticipationRequestDto.builder()
                    .id(requestId)
                    .created(LocalDateTime.now())
                    .event(1L)
                    .requester(userId)
                    .status(RequestStatus.valueOf(RequestStatus.CANCELED.name()))
                    .build();
        });

        mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        verify(requestService, times(1)).cancelRequest(eq(1L), eq(1L));
    }
}