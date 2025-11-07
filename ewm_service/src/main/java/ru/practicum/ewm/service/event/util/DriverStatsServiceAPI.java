package ru.practicum.ewm.service.event.util;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DriverStatsServiceAPI {

    void post(HttpServletRequest httpServletRequest);

    List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
