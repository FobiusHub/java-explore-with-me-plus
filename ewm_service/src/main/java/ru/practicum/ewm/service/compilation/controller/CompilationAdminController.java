package ru.practicum.ewm.service.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.compilation.dto.*;
import ru.practicum.ewm.service.compilation.service.CompilationAdminService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {

    private final CompilationAdminService service;  // ← интерфейс

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        service.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody UpdateCompilationRequest dto) {
        log.info("АДМИН API: PATCH /admin/compilations/{} тело: events={}, pinned={}, title={}",
                compId, dto.getEvents(), dto.getPinned(), dto.getTitle());
        CompilationDto resp = service.update(compId, dto);
        log.info("АДМИН API: результат PATCH: id={}, кол-во событий={}, pinned={}, title={}",
                resp.getId(), resp.getEvents() == null ? 0 : resp.getEvents().size(), resp.getPinned(), resp.getTitle());
        return resp;
    }
}