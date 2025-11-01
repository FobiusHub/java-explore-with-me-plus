package ru.practicum.ewm.service.event.repository.filter.admin;


import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.model.Event;

import java.util.List;

@Repository
public interface AdminEventFilterRepository {
        List<Event> findAllByFilter(AdminEventFilterDto adminEventFilter);
}
