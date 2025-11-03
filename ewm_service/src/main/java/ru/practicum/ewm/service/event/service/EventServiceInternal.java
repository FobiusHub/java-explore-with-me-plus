package ru.practicum.ewm.service.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.event.util.EventBuilder;
import ru.practicum.ewm.service.event.util.EventMapper;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceInternal {

    private final EventBuilder eventBuilder;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter) {
        return eventRepository.findByFilter(filter).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto postEvent(NewEventDto newEventDto) {
        Long userId = newEventDto.getInitiatorId();
        Event eventToSave = eventBuilder.buildEventBy(newEventDto);
        Event savedEvent = eventRepository.save(eventToSave);
        log.info("GET /users/{}/events response:{}", userId, savedEvent);
        return EventMapper.toEventFullDto(savedEvent);
    }

    public EventFullDto getEventOfUserBy(InternalEventFilter filter) {
        if (!userRepository.existsById(filter.userId())) {
            String message = String.format("User with id=%d is not exists", filter.userId());
            throw new NotFoundException(message);
        }
        String message = String.format("Event with id=%d not found", filter.eventId());
        List<Event> events = eventRepository.findByFilter(filter);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException(message));
    }
}
