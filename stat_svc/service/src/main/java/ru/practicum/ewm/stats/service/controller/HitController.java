package ru.practicum.ewm.stats.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.service.HitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public void create(@Valid @RequestBody EndpointHitDto hitDto) {
        hitService.create(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        return hitService.viewStats(start, end, uris, unique);
    }
}
