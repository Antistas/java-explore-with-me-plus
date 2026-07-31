package ru.practicum.gateway.ewm.event;

import ru.practicum.gateway.ewm.category.CategoryMapper;
import ru.practicum.gateway.ewm.event.dto.*;
import ru.practicum.gateway.ewm.model.Category;
import ru.practicum.gateway.ewm.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        String createdStr = event.getCreatedOn() != null ? event.getCreatedOn().format(FORMATTER) : null;
        String eventDateStr = event.getEventDate() != null ? event.getEventDate().format(FORMATTER) : null;
        String publishedStr = event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null;

        UserShortDto initiatorDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());
        LocationDto locationDto = new LocationDto(event.getLat(), event.getLon());

        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests,
                createdStr,
                event.getDescription(),
                eventDateStr,
                initiatorDto,
                locationDto,
                event.getPaid(),
                event.getParticipantLimit(),
                publishedStr,
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                views
        );
    }


    public static EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        String eventDateStr = event.getEventDate() != null ? event.getEventDate().format(FORMATTER) : null;
        UserShortDto initiatorDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());

        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests,
                eventDateStr,
                initiatorDto,
                event.getPaid(),
                event.getTitle(),
                views
        );
    }


    public static void updateEventWithAdminRequest(Event event, UpdateEventAdminRequest request, Category newCategory) {
        if (request.annotation() != null) {
            event.setAnnotation(request.annotation());
        }
        if (request.description() != null) {
            event.setDescription(request.description());
        }
        if (request.title() != null) {
            event.setTitle(request.title());
        }
        if (request.eventDate() != null) {
            event.setEventDate(LocalDateTime.parse(request.eventDate(), FORMATTER));
        }
        if (newCategory != null) {
            event.setCategory(newCategory);
        }
        if (request.location() != null) {
            event.setLat(request.location().lat());
            event.setLon(request.location().lon());
        }
        if (request.paid() != null) {
            event.setPaid(request.paid());
        }
        if (request.participantLimit() != null) {
            event.setParticipantLimit(request.participantLimit());
        }
        if (request.requestModeration() != null) {
            event.setRequestModeration(request.requestModeration());
        }
    }
}