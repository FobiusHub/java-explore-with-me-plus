package ru.practicum.ewm.stats.service.service;

import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.util.List;

public interface HitService {
    void create(EndpointHitDto hitDto);

    List<ViewStatsDto> viewStats(String start, String end, List<String> uris, boolean unique);
}
