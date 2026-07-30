package ru.practicum.gateway.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.ewm.compilation.dto.CompilationDto;
import ru.practicum.gateway.ewm.event.service.PublicEventService;
import ru.practicum.gateway.ewm.exception.NotFoundException;
import ru.practicum.gateway.ewm.model.Compilation;
import ru.practicum.gateway.ewm.model.EventState;
import ru.practicum.gateway.ewm.repository.CompilationRepository;
import ru.practicum.gateway.ewm.util.OffsetPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final PublicEventService eventService;

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        OffsetPageRequest page = new OffsetPageRequest(from, size, Sort.by("id"));
        Page<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(page)
                : compilationRepository.findByPinned(pinned, page);
        return compilations.stream().map(this::toDto).toList();
    }

    public CompilationDto getCompilation(long compilationId) {
        return compilationRepository.findById(compilationId)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Compilation id=" + compilationId + " was not found"));
    }

    private CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                eventService.toShortDtos(compilation.getEvents().stream()
                        .filter(event -> event.getState() == EventState.PUBLISHED)
                        .toList()),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
