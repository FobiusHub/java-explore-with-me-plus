package ru.practicum.ewm.service.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.exception.TitleAlreadyExistsException;
import ru.practicum.ewm.service.compilation.service.CompilationAdminService;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CompilationAdminController.class)
class CompilationAdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CompilationAdminService compilationAdminService;

    private NewCompilationDto newCompilationDto;
    private UpdateCompilationRequest updateCompilationRequest;
    private CompilationDto compilationDto;

    @BeforeEach
    void setUp() {
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

        newCompilationDto = NewCompilationDto.builder()
                .title("Новая подборка")
                .pinned(true)
                .events(Set.of(1L, 2L))
                .build();

        updateCompilationRequest = UpdateCompilationRequest.builder()
                .title("Обновленная подборка")
                .pinned(false)
                .events(Set.of(3L, 4L))
                .build();

        compilationDto = CompilationDto.builder()
                .id(1L)
                .title("Новая подборка")
                .pinned(true)
                .events(List.of(eventShortDto))
                .build();
    }

    @Test
    void createCompilation_shouldCreateCompilation() throws Exception {
        when(compilationAdminService.create(any(NewCompilationDto.class)))
                .thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.pinned").value(compilationDto.getPinned()));

        verify(compilationAdminService, times(1)).create(any(NewCompilationDto.class));
    }

    @Test
    void createCompilation_shouldReturnConflict_whenTitleAlreadyExists() throws Exception {
        when(compilationAdminService.create(any(NewCompilationDto.class)))
                .thenThrow(new TitleAlreadyExistsException("Подборка с таким названием уже существует"));

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(compilationAdminService, times(1)).create(any(NewCompilationDto.class));
    }

    @Test
    void deleteCompilation_shouldDeleteCompilation() throws Exception {
        Long compId = 1L;
        doNothing().when(compilationAdminService).delete(anyLong());

        mvc.perform(delete("/admin/compilations/{compId}", compId))
                .andExpect(status().isNoContent());

        verify(compilationAdminService, times(1)).delete(compId);
    }

    @Test
    void deleteCompilation_shouldReturnNotFound_whenCompilationNotExists() throws Exception {
        Long compId = 1L;
        doThrow(new CompilationNotFoundException(compId))
                .when(compilationAdminService).delete(anyLong());

        mvc.perform(delete("/admin/compilations/{compId}", compId))
                .andExpect(status().isNotFound());

        verify(compilationAdminService, times(1)).delete(compId);
    }

    @Test
    void updateCompilation_shouldUpdateCompilation() throws Exception {
        Long compId = 1L;
        when(compilationAdminService.update(anyLong(), any(UpdateCompilationRequest.class)))
                .thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/{compId}", compId)
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.pinned").value(compilationDto.getPinned()));

        verify(compilationAdminService, times(1)).update(anyLong(), any(UpdateCompilationRequest.class));
    }

    @Test
    void updateCompilation_shouldReturnNotFound_whenCompilationNotExists() throws Exception {
        Long compId = 1L;
        when(compilationAdminService.update(anyLong(), any(UpdateCompilationRequest.class)))
                .thenThrow(new CompilationNotFoundException(compId));

        mvc.perform(patch("/admin/compilations/{compId}", compId)
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(compilationAdminService, times(1)).update(anyLong(), any(UpdateCompilationRequest.class));
    }

    @Test
    void updateCompilation_shouldReturnConflict_whenTitleAlreadyExists() throws Exception {
        Long compId = 1L;
        when(compilationAdminService.update(anyLong(), any(UpdateCompilationRequest.class)))
                .thenThrow(new TitleAlreadyExistsException("Подборка с таким названием уже существует"));

        mvc.perform(patch("/admin/compilations/{compId}", compId)
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(compilationAdminService, times(1)).update(anyLong(), any(UpdateCompilationRequest.class));
    }

    @Test
    void updateCompilation_withEmptyBody_shouldUpdateCompilation() throws Exception {
        Long compId = 1L;
        when(compilationAdminService.update(anyLong(), any(UpdateCompilationRequest.class)))
                .thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/{compId}", compId)
                        .content("{}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compilationDto.getId()))
                .andExpect(jsonPath("$.title").value(compilationDto.getTitle()))
                .andExpect(jsonPath("$.pinned").value(compilationDto.getPinned()));

        verify(compilationAdminService, times(1)).update(anyLong(), any(UpdateCompilationRequest.class));
    }

    @Test
    void createCompilation_shouldReturnBadRequest_whenInvalidData() throws Exception {
        String invalidDtoJson = "{\n" +
                "  \"title\": \"\",\n" +
                "  \"pinned\": true,\n" +
                "  \"events\": [1, 2]\n" +
                "}";

        mvc.perform(post("/admin/compilations")
                        .content(invalidDtoJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String invalidDtoJson2 = "{\n" +
                "  \"title\": null,\n" +
                "  \"pinned\": true,\n" +
                "  \"events\": [1, 2]\n" +
                "}";

        mvc.perform(post("/admin/compilations")
                        .content(invalidDtoJson2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}