package ru.practicum.ewm.service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.util.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("PUBLIC: запрос подборок: pinned={}, from={}, size={}", pinned, from, size);
        var pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = repository.findCompilations(pinned, pageable);

        return compilations.stream()
                .map(this::mapCompilationWithEvents)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        log.info("PUBLIC: запрос подборки id={}", compId);
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
        return mapCompilationWithEvents(compilation);
    }

    private CompilationDto mapCompilationWithEvents(Compilation compilation) {
        Set<Long> event = compilation.getEvents();
        if (event == null || event.isEmpty()) {
            return CompilationMapper.toDto(compilation, List.of());
        }
        List<EventShortDto> events = eventRepository.findAllById(event).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        return CompilationMapper.toDto(compilation, events);
    }
}
