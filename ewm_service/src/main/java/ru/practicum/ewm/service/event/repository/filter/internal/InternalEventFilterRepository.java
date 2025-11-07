package ru.practicum.ewm.service.event.repository.filter.internal;


import ru.practicum.ewm.service.event.model.Event;

import java.util.List;

public interface InternalEventFilterRepository {
    List<Event> findByFilter(InternalEventFilter filter);
}