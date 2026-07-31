package ru.practicum.gateway.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.ewm.event.dto.EventFullDto;
import ru.practicum.gateway.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.gateway.ewm.exception.ConflictException;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.ewm.model.Category;
import ru.practicum.gateway.ewm.model.Event;
import ru.practicum.gateway.ewm.model.EventState;
import ru.practicum.gateway.ewm.model.EventStateAction;
import ru.practicum.gateway.ewm.model.RequestStatus;
import ru.practicum.gateway.ewm.repository.CategoryRepository;
import ru.practicum.gateway.ewm.repository.EventRepository;
import ru.practicum.gateway.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    @Override
    public List<EventFullDto> searchEventsAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            int from,
            int size) {

        log.info("Admin Search Events: users={}, states={}, categories={}, start={}, end={}",
                users, states, categories, rangeStart, rangeEnd);

        // Конвертируем строки статусов в Enum
        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        // Парсим даты
        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, FORMATTER) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, FORMATTER) : null;

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findEventsByAdmin(users, eventStates, categories, start, end, pageable);

        return events.stream()
                .map(event -> {
                    // Считаем подтвержденные запросы для каждого найденного события
                    long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                    // Просмотры временно ставим в 0 (будут подтягиваться позже через сервис статистики)
                    long views = 0L;
                    return EventMapper.toEventFullDto(event, confirmedRequests, views);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Admin Update Event id={}: {}", eventId, updateRequest);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        // Если пришла новая категория — проверяем её существование
        Category newCategory = null;
        if (updateRequest.category() != null) {
            newCategory = categoryRepository.findById(updateRequest.category())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + updateRequest.category() + " не найдена"));
        }

        // Бизнес-правило: проверка даты начала события при обновлении/публикации
        LocalDateTime updatedDate = updateRequest.eventDate() != null
                ? LocalDateTime.parse(updateRequest.eventDate(), FORMATTER)
                : event.getEventDate();

        // Модерация статуса события (stateAction)
        if (updateRequest.stateAction() != null) {
            EventStateAction action = EventStateAction.valueOf(updateRequest.stateAction());

            if (action == EventStateAction.PUBLISH_EVENT) {
                // Событие можно опубликовать только если оно ожидает модерации (PENDING)
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Невозможно опубликовать событие, так как оно находится в состоянии: " + event.getState());
                }

                // Дата начала события должна быть не ранее чем за час от даты публикации
                if (updatedDate.isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Дата начала события должна быть не ранее чем за час от даты публикации");
                }

                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            else if (action == EventStateAction.REJECT_EVENT) {
                // Событие можно отклонить только если оно еще не опубликовано
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Невозможно отклонить опубликованное событие");
                }
                event.setState(EventState.CANCELED);
            }
        }

        // Обновляем остальные поля события через маппер
        EventMapper.updateEventWithAdminRequest(event, updateRequest, newCategory);

        Event savedEvent = eventRepository.save(event);

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = 0L; // Будет интегрировано со статистикой на следующем этапе

        return EventMapper.toEventFullDto(savedEvent, confirmedRequests, views);
    }
}