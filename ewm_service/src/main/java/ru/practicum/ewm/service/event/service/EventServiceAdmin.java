package ru.practicum.ewm.service.event.service;

import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;

import java.util.List;

public interface EventServiceAdmin {
    List<EventFullDto> getEventsFilteredBy(AdminEventFilter adminEventFilter);

    EventFullDto patchEvent(UpdateEventAdminRequest event, Long eventId);
}
