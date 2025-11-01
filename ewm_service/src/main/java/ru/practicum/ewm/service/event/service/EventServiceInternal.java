package ru.practicum.ewm.service.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventJpaRepository;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilterDto;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceInternal {

    private final EventJpaRepository eventJpaRepository;

    public List<EventShortDto> getEventsOfUserById(InternalEventFilterDto internalEventFilterDto) {
        return eventJpaRepository.findAllByFilter(internalEventFilterDto).stream()
                .map(Event::toEventShortDto)
                .toList();
    }
}
