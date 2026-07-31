package ru.practicum.gateway.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.ewm.compilation.dto.CompilationDto;
import ru.practicum.gateway.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.gateway.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.gateway.ewm.event.service.PublicEventService;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.ewm.model.Compilation;
import ru.practicum.gateway.ewm.model.Event;
import ru.practicum.gateway.ewm.repository.CompilationRepository;
import ru.practicum.gateway.ewm.repository.EventRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final PublicEventService eventService;

    public CompilationDto createCompilation(NewCompilationDto request) {
        Compilation compilation = new Compilation();
        compilation.setTitle(request.title());
        compilation.setPinned(Boolean.TRUE.equals(request.pinned()));
        compilation.setEvents(loadEvents(request.events()));

        return toDto(compilationRepository.save(compilation));
    }

    public CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest request) {
        Compilation compilation = findCompilation(compilationId);

        if (request.title() != null) {
            compilation.setTitle(request.title());
        }
        if (request.pinned() != null) {
            compilation.setPinned(request.pinned());
        }
        if (request.events() != null) {
            compilation.setEvents(loadEvents(request.events()));
        }

        return toDto(compilationRepository.save(compilation));
    }

    public void deleteCompilation(long compilationId) {
        compilationRepository.delete(findCompilation(compilationId));
    }

    private Compilation findCompilation(long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(
                        "Compilation with id=" + compilationId + " was not found"));
    }

    private Set<Event> loadEvents(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(eventRepository.findAllById(eventIds));
    }

    private CompilationDto toDto(Compilation compilation) {
        List<Event> events = List.copyOf(compilation.getEvents());
        return new CompilationDto(
                compilation.getId(),
                eventService.toShortDtos(events),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
