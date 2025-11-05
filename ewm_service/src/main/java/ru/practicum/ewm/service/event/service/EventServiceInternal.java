package ru.practicum.ewm.service.event.service;

import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;

import java.util.List;

public interface EventServiceInternal {
    List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter);

    EventFullDto postEvent(NewEventDto newEventDto);

    EventFullDto getEventOfUserBy(InternalEventFilter filter);

    EventFullDto getRequestOfUserBy(Long userId, Long eventId);
}
