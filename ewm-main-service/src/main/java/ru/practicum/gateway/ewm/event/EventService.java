package ru.practicum.gateway.ewm.event;

import ru.practicum.gateway.ewm.event.dto.EventFullDto;
import ru.practicum.gateway.ewm.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {


    List<EventFullDto> searchEventsAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    );


    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest);
}