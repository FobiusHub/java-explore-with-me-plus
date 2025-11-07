package ru.practicum.ewm.service.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.enums.EventState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationPublicServiceImplTest {

    @Mock
    private CompilationRepository repository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompilationPublicServiceImpl compilationPublicService;

    private Compilation compilation;

    @BeforeEach
    void setUp() {
        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);
        compilation.setEvents(Set.of(1L, 2L));
    }

    @Test
    void getCompilations_shouldReturnListOfCompilationDto() {
        // Подготавливаем данные
        when(repository.findCompilations(anyBoolean(), any())).thenReturn(List.of(compilation));

        Event event1 = new Event();
        event1.setId(1L);
        event1.setState(EventState.PUBLISHED);
        Event event2 = new Event();
        event2.setId(2L);
        event2.setState(EventState.PUBLISHED);
        when(eventRepository.findAllById(anySet())).thenReturn(List.of(event1, event2));

        // Выполняем операцию
        List<CompilationDto> result = compilationPublicService.getCompilations(true, 0, 10);

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(compilation.getId(), result.getFirst().getId());
        assertEquals(compilation.getTitle(), result.getFirst().getTitle());
        assertEquals(compilation.isPinned(), result.getFirst().getPinned());

        // Проверяем вызовы
        verify(repository, times(1)).findCompilations(anyBoolean(), any());
        verify(eventRepository, times(1)).findAllById(anySet());
    }

    @Test
    void getCompilations_shouldReturnEmptyList_whenNoCompilationsFound() {
        // Подготавливаем данные
        when(repository.findCompilations(anyBoolean(), any())).thenReturn(List.of());

        // Выполняем операцию
        List<CompilationDto> result = compilationPublicService.getCompilations(true, 0, 10);

        // Проверяем результаты
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Проверяем вызовы
        verify(repository, times(1)).findCompilations(anyBoolean(), any());
        verify(eventRepository, never()).findAllById(anySet());
    }

    @Test
    void getById_shouldReturnCompilationDto_whenCompilationExists() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.findById(compId)).thenReturn(Optional.of(compilation));

        Event event1 = new Event();
        event1.setId(1L);
        event1.setState(EventState.PUBLISHED);
        Event event2 = new Event();
        event2.setId(2L);
        event2.setState(EventState.PUBLISHED);
        when(eventRepository.findAllById(anySet())).thenReturn(List.of(event1, event2));

        // Выполняем операцию
        CompilationDto result = compilationPublicService.getById(compId);

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(compilation.getId(), result.getId());
        assertEquals(compilation.getTitle(), result.getTitle());
        assertEquals(compilation.isPinned(), result.getPinned());

        // Проверяем вызовы
        verify(repository, times(1)).findById(compId);
        verify(eventRepository, times(1)).findAllById(anySet());
    }

    @Test
    void getById_shouldThrowCompilationNotFoundException_whenCompilationNotExists() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.findById(compId)).thenReturn(Optional.empty());

        // Выполняем операцию и проверяем исключение
        assertThrows(CompilationNotFoundException.class, () -> compilationPublicService.getById(compId));

        // Проверяем вызовы
        verify(repository, times(1)).findById(compId);
        verify(eventRepository, never()).findAllById(anySet());
    }

    @Test
    void getCompilations_shouldHandleCompilationWithoutEvents() {
        // Подготавливаем данные
        Compilation compilationWithoutEvents = new Compilation();
        compilationWithoutEvents.setId(2L);
        compilationWithoutEvents.setTitle("Empty Compilation");
        compilationWithoutEvents.setPinned(false);
        compilationWithoutEvents.setEvents(Set.of()); // Нет событий

        when(repository.findCompilations(anyBoolean(), any())).thenReturn(List.of(compilationWithoutEvents));

        // Выполняем операцию
        List<CompilationDto> result = compilationPublicService.getCompilations(false, 0, 10);

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(compilationWithoutEvents.getId(), result.getFirst().getId());
        assertEquals(compilationWithoutEvents.getTitle(), result.getFirst().getTitle());
        assertNotNull(result.getFirst().getEvents());
        assertTrue(result.getFirst().getEvents().isEmpty());

        // Проверяем вызовы
        verify(repository, times(1)).findCompilations(anyBoolean(), any());
        verify(eventRepository, never()).findAllById(anySet());
    }
}