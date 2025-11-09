package ru.practicum.ewm.service.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.service.compilation.dto.*;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    /**
     * Создаёт новую подборку по DTO.
     * Важно: коллекции должны быть ИЗМЕНЯЕМЫМИ (HashSet),
     * иначе Hibernate может не выполнять dirty-checking.
     */
    public Compilation toEntity(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        Set<Long> events = dto.getEvents() != null
                ? new HashSet<>(dto.getEvents())
                : new HashSet<>();

        compilation.setEvents(events);

        return compilation;
    }

    /**
     * Преобразует подборку в DTO.
     */
    public CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events != null ? events : List.of())
                .build();
    }
}
