package ru.practicum.ewm.service.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.filter.open.OpenEventFilter;

import java.util.List;

public interface EventServiceOpen {
    List<EventShortDto> getEventsFilteredBy(OpenEventFilter filter, HttpServletRequest httpServletRequest);

    EventFullDto getEventById(Long id);
}
