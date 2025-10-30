package ru.practicum.ewm.service.event.repository.EventFilterAdmin;


import ru.practicum.ewm.service.event.model.Event;

import java.util.List;

public interface AdminEventFilterRepository {
        List<Event> findAllByFilter(AdminEventFilter adminEventFilter);
}
