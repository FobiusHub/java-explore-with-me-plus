package ru.practicum.ewm.service.event.service;

import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.event.dto.UpdateRequestDto;

import java.util.List;

public interface EventServiceInternal {
    List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter);

    EventFullDto postEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getEventOfUserBy(InternalEventFilter filter);

    ParticipationRequestDto getRequestOfUserBy(Long userId, Long eventId);

    List<ParticipationRequestDto> updateRequestStatus(UpdateRequestDto updateRequestDto, Long userId, Long eventId);

    EventFullDto patchEventOfUserBy(UpdateEventUserRequest updateEvent, Long userId, Long eventId);
}
