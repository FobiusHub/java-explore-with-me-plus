package ru.practicum.ewm.service.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.service.compilation.dto.*;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class CompilationMapper {

    public Compilation toEntity(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        compilation.setEvents(dto.getEvents() != null ? dto.getEvents() : Collections.emptySet());
        return compilation;
    }

    public void updateEntity(UpdateCompilationRequest dto, Compilation compilation) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(dto.getEvents());
        }
    }

    public CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        List<EventShortDto> safeEvents = events == null ? List.of() : events;
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(safeEvents)
                .build();
    }
}