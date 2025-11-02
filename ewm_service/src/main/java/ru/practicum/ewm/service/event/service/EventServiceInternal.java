package ru.practicum.ewm.service.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.event.util.EventBuilder;
import ru.practicum.ewm.service.event.util.EventMapper;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceInternal {

    private final EventBuilder eventBuilder;
    private final EventRepository eventRepository;

    public List<EventShortDto> getEventsOfUserBy(InternalEventFilter internalEventFilterDto) {
        return eventRepository.findAllByFilter(internalEventFilterDto).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto postEvent(NewEventDto newEventDto) {
        Long userId = newEventDto.getInitiatorId();
        Event eventToSave = eventBuilder.buildEventBy(newEventDto);
        Event savedEvent = eventRepository.save(eventToSave);

        log.info("GET /users/{}/events with body:{}", userId, newEventDto);

        return EventMapper.toEventFullDto(savedEvent);
    }
}
