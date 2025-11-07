package ru.practicum.ewm.service.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.exception.TitleAlreadyExistsException;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.enums.EventState;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationAdminServiceImplTest {

    @Mock
    private CompilationRepository repository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompilationAdminServiceImpl compilationAdminService;

    private Compilation compilation;
    private NewCompilationDto newCompilationDto;
    private UpdateCompilationRequest updateCompilationRequest;

    @BeforeEach
    void setUp() {
        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);
        compilation.setEvents(Set.of(1L, 2L));

        newCompilationDto = NewCompilationDto.builder()
                .title("Test Compilation")
                .pinned(true)
                .events(Set.of(1L, 2L))
                .build();

        updateCompilationRequest = UpdateCompilationRequest.builder()
                .title("Updated Compilation")
                .pinned(false)
                .events(Set.of(3L, 4L))
                .build();
    }

    @Test
    void create_ShouldCreateCompilation_WhenValidDataProvided() {
        // Подготавливаем данные
        when(repository.existsByTitle(anyString())).thenReturn(false);
        when(repository.save(any(Compilation.class))).thenReturn(compilation);

        Event event1 = new Event();
        event1.setId(1L);
        event1.setState(EventState.PUBLISHED);
        Event event2 = new Event();
        event2.setId(2L);
        event2.setState(EventState.PUBLISHED);
        when(eventRepository.findAllById(anySet())).thenReturn(List.of(event1, event2));

        // Выполняем операцию
        CompilationDto result = compilationAdminService.create(newCompilationDto);

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(compilation.getId(), result.getId());
        assertEquals(compilation.getTitle(), result.getTitle());
        assertEquals(compilation.isPinned(), result.getPinned());
        assertNotNull(result.getEvents());

        // Проверяем вызовы
        verify(repository, times(1)).existsByTitle(anyString());
        verify(repository, times(1)).save(any(Compilation.class));
        verify(eventRepository, times(1)).findAllById(anySet());
    }

    @Test
    void create_ShouldThrowTitleAlreadyExistsException_WhenTitleAlreadyExists() {
        // Подготавливаем данные
        when(repository.existsByTitle(anyString())).thenReturn(true);

        // Выполняем операцию и проверяем исключение
        assertThrows(TitleAlreadyExistsException.class, () -> compilationAdminService.create(newCompilationDto));

        // Проверяем вызовы
        verify(repository, times(1)).existsByTitle(anyString());
        verify(repository, never()).save(any(Compilation.class));
        verify(eventRepository, never()).findAllById(anySet());
    }

    @Test
    void delete_ShouldDeleteCompilation_WhenCompilationExists() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.existsById(compId)).thenReturn(true);

        // Выполняем операцию
        compilationAdminService.delete(compId);

        // Проверяем вызовы
        verify(repository, times(1)).existsById(compId);
        verify(repository, times(1)).deleteById(compId);
    }

    @Test
    void delete_ShouldThrowCompilationNotFoundException_WhenCompilationNotExists() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.existsById(compId)).thenReturn(false);

        // Выполняем операцию и проверяем исключение
        assertThrows(CompilationNotFoundException.class, () -> compilationAdminService.delete(compId));

        // Проверяем вызовы
        verify(repository, times(1)).existsById(compId);
        verify(repository, never()).deleteById(compId);
    }

    @Test
    void update_ShouldUpdateCompilation_WhenValidDataProvided() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.findById(compId)).thenReturn(Optional.of(compilation));
        when(repository.existsByTitle(anyString())).thenReturn(false);
        when(repository.save(any(Compilation.class))).thenReturn(compilation);

        Event event1 = new Event();
        event1.setId(3L);
        event1.setState(EventState.PUBLISHED);
        Event event2 = new Event();
        event2.setId(4L);
        event2.setState(EventState.PUBLISHED);
        when(eventRepository.findAllById(anySet())).thenReturn(List.of(event1, event2));

        // Выполняем операцию
        CompilationDto result = compilationAdminService.update(compId, updateCompilationRequest);

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(compId, result.getId());
        assertEquals(updateCompilationRequest.getTitle(), result.getTitle());
        assertEquals(updateCompilationRequest.getPinned(), result.getPinned());
        assertNotNull(result.getEvents());

        // Проверяем вызовы
        verify(repository, times(1)).findById(compId);
        verify(repository, times(1)).existsByTitle(anyString());
        verify(repository, times(1)).save(any(Compilation.class));
        verify(eventRepository, times(1)).findAllById(anySet());
    }

    @Test
    void update_ShouldThrowCompilationNotFoundException_WhenCompilationNotExists() {
        // Подготавливаем данные
        Long compId = 1L;
        when(repository.findById(compId)).thenReturn(Optional.empty());

        // Выполняем операцию и проверяем исключение
        assertThrows(CompilationNotFoundException.class, () -> compilationAdminService.update(compId, updateCompilationRequest));

        // Проверяем вызовы
        verify(repository, times(1)).findById(compId);
        verify(repository, never()).existsByTitle(anyString());
        verify(repository, never()).save(any(Compilation.class));
        verify(eventRepository, never()).findAllById(anySet());
    }

    @Test
    void update_ShouldThrowTitleAlreadyExistsException_WhenTitleAlreadyExists() {
        // Подготавливаем данные
        Long compId = 1L;
        String existingTitle = "Existing Title";
        Compilation existingCompilation = new Compilation();
        existingCompilation.setId(compId);
        existingCompilation.setTitle("Old Title");

        when(repository.findById(compId)).thenReturn(Optional.of(existingCompilation));
        when(repository.existsByTitle(existingTitle)).thenReturn(true);

        UpdateCompilationRequest request = UpdateCompilationRequest.builder()
                .title(existingTitle)
                .build();

        // Выполняем операцию и проверяем исключение
        assertThrows(TitleAlreadyExistsException.class, () -> compilationAdminService.update(compId, request));

        // Проверяем вызовы
        verify(repository, times(1)).findById(compId);
        verify(repository, times(1)).existsByTitle(existingTitle);
        verify(repository, never()).save(any(Compilation.class));
        verify(eventRepository, never()).findAllById(anySet());
    }
}