package ru.practicum.ewm.service.compilation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.service.CompilationPublicService;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CompilationPublicController.class)
class CompilationPublicControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompilationPublicService compilationPublicService;

    @Test
    void getCompilations_shouldReturnListOfCompilations() throws Exception {
        // Подготавливаем данные
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Концерты");

        UserShortDto userShortDto = UserShortDto.builder()
                .id(1L)
                .name("Пользователь 1")
                .build();

        EventShortDto eventShortDto = EventShortDto.builder()
                .id(1L)
                .annotation("Краткое описание события 1")
                .category(categoryDto)
                .confirmedRequests(5L)
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(userShortDto)
                .paid(true)
                .title("Событие 1")
                .views(10L)
                .build();

        CompilationDto compilationDto = CompilationDto.builder()
                .id(1L)
                .title("Тестовая подборка")
                .pinned(true)
                .events(List.of(eventShortDto))
                .build();

        when(compilationPublicService.getCompilations(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(compilationDto));

        // Выполняем запрос
        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.[0].title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.[0].pinned").value(compilationDto.getPinned()));

        verify(compilationPublicService, times(1)).getCompilations(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getCompilations_shouldReturnListOfCompilations_withoutParams() throws Exception {
        // Подготавливаем данные
        CompilationDto compilationDto = CompilationDto.builder()
                .id(1L)
                .title("Тестовая подборка")
                .pinned(true)
                .events(List.of())
                .build();

        when(compilationPublicService.getCompilations(isNull(), anyInt(), anyInt()))
                .thenReturn(List.of(compilationDto));

        // Выполняем запрос
        mvc.perform(get("/compilations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.[0].title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.[0].pinned").value(compilationDto.getPinned()));

        verify(compilationPublicService, times(1)).getCompilations(isNull(), anyInt(), anyInt());
    }

    @Test
    void getCompilations_shouldReturnEmptyList_whenNoCompilationsFound() throws Exception {
        // Нет подборок
        when(compilationPublicService.getCompilations(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of());

// Выполняем запрос
        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(compilationPublicService, times(1)).getCompilations(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getCompilations_shouldReturnBadRequest_whenInvalidPaginationParams() throws Exception {
        // Выполняем запрос с недопустимыми параметрами пагинации
        mvc.perform(get("/compilations")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().is4xxClientError());

        mvc.perform(get("/compilations")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().is4xxClientError());

        //Проверяем, что сервис не был вызван
        verify(compilationPublicService, never()).getCompilations(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getById_shouldReturnCompilation() throws Exception {
        // Подготавливаем данные
        Long compId = 1L;

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Концерты");

        UserShortDto userShortDto = UserShortDto.builder()
                .id(1L)
                .name("Пользователь 1")
                .build();

        EventShortDto eventShortDto = EventShortDto.builder()
                .id(1L)
                .annotation("Краткое описание события 1")
                .category(categoryDto)
                .confirmedRequests(5L)
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(userShortDto)
                .paid(true)
                .title("Событие 1")
                .views(10L)
                .build();

        CompilationDto compilationDto = CompilationDto.builder()
                .id(compId)
                .title("Тестовая подборка")
                .pinned(true)
                .events(List.of(eventShortDto))
                .build();

        when(compilationPublicService.getById(compId))
                .thenReturn(compilationDto);

        // Выполняем запрос
        mvc.perform(get("/compilations/{compId}", compId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.pinned").value(compilationDto.getPinned()));

        verify(compilationPublicService, times(1)).getById(compId);
    }

    @Test
    void getById_shouldReturnNotFound_whenCompilationNotExists() throws Exception {
        // Подготавливаем данные
        Long compId = 1L;

        when(compilationPublicService.getById(compId))
                .thenThrow(new CompilationNotFoundException(compId));

        // Выполняем запрос
        mvc.perform(get("/compilations/{compId}", compId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isNotFound());

        verify(compilationPublicService, times(1)).getById(compId);
    }

    @Test
    void getById_shouldReturnBadRequest_whenInvalidId() throws Exception {
        // Выполняем запрос с недопустимым ID
        mvc.perform(get("/compilations/{compId}", "invalid")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());

        // Проверяем, что сервис не был вызван
        verify(compilationPublicService, never()).getById(anyLong());
    }
}