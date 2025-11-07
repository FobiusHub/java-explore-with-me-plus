package ru.practicum.ewm.service.event.service;

import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventServiceInternal {
    List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter);

    EventFullDto postEvent(NewEventDto newEventDto);

    EventFullDto getEventOfUserBy(InternalEventFilter filter);

    ParticipationRequestDto getRequestOfUserBy(Long userId, Long eventId);

    void updateRequestStatus(UpdateRequestDto updateRequestDto, Long userId, Long eventId);

    EventFullDto patchEventOfUserBy(UpdateEventUserRequest updateEvent);
}
