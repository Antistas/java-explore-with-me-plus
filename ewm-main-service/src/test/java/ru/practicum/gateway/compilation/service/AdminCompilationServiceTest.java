package ru.practicum.gateway.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.gateway.compilation.dto.CompilationDto;
import ru.practicum.gateway.compilation.dto.NewCompilationDto;
import ru.practicum.gateway.compilation.dto.UpdateCompilationRequest;
import ru.practicum.gateway.event.service.PublicEventService;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.compilation.model.Compilation;
import ru.practicum.gateway.event.model.Event;
import ru.practicum.gateway.compilation.repository.CompilationRepository;
import ru.practicum.gateway.event.repository.EventRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCompilationServiceTest {
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private PublicEventService eventService;

    private AdminCompilationService compilationService;

    @BeforeEach
    void setUp() {
        compilationService = new AdminCompilationService(
                compilationRepository,
                eventRepository,
                eventService
        );
    }

    @Test
    void createCompilationUsesDefaultPinnedAndSavesEvents() {
        Event first = event(1L);
        Event second = event(2L);
        when(eventRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(first, second));
        when(compilationRepository.save(any(Compilation.class))).thenAnswer(invocation -> {
            Compilation compilation = invocation.getArgument(0);
            compilation.setId(10L);
            return compilation;
        });
        when(eventService.toShortDtos(any())).thenReturn(List.of());

        CompilationDto result = compilationService.createCompilation(
                new NewCompilationDto(Set.of(1L, 2L), null, "Летние события")
        );

        assertEquals(10L, result.id());
        assertEquals("Летние события", result.title());
        assertFalse(result.pinned());
        verify(compilationRepository).save(any(Compilation.class));
    }

    @Test
    void updateCompilationChangesOnlyProvidedFieldsAndClearsEvents() {
        Compilation compilation = new Compilation();
        compilation.setId(7L);
        compilation.setTitle("Старое название");
        compilation.setPinned(true);
        compilation.setEvents(new LinkedHashSet<>(Set.of(event(3L))));

        when(compilationRepository.findById(7L)).thenReturn(Optional.of(compilation));
        when(compilationRepository.save(compilation)).thenReturn(compilation);
        when(eventService.toShortDtos(any())).thenReturn(List.of());

        CompilationDto result = compilationService.updateCompilation(
                7L,
                new UpdateCompilationRequest(Set.of(), null, "Новое название")
        );

        assertEquals("Новое название", result.title());
        assertTrue(result.pinned());
        assertTrue(compilation.getEvents().isEmpty());
    }

    @Test
    void updateMissingCompilationThrowsNotFound() {
        when(compilationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> compilationService.updateCompilation(
                        404L,
                        new UpdateCompilationRequest(null, null, "Новое название")
                )
        );
    }

    @Test
    void deleteCompilationDeletesExistingEntity() {
        Compilation compilation = new Compilation();
        compilation.setId(5L);
        when(compilationRepository.findById(5L)).thenReturn(Optional.of(compilation));

        compilationService.deleteCompilation(5L);

        verify(compilationRepository).delete(compilation);
    }

    private Event event(long id) {
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
