package ru.practicum.gateway.request.mapper;

import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.request.dto.ParticipationRequestDto;
import ru.practicum.gateway.request.model.ParticipationRequest;
import ru.practicum.gateway.request.model.RequestStatus;
import ru.practicum.gateway.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        if (request == null) {
            return null;
        }

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static ParticipationRequest toEntity(Event event, User requester, RequestStatus status) {
        if (event == null || requester == null) {
            return null;
        }

        return ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .status(status)
                .build();
    }

    public static List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}