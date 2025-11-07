package ru.practicum.ewm.service.event.service;

import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;

import java.util.List;

public interface EventServiceAdmin {
    List<EventShortDto> getEventsFilteredBy(AdminEventFilter adminEventFilter);

    EventShortDto patchEvent(UpdateEventAdminRequest event, Long eventId);
}
