package ru.practicum.ewm.service.event.service;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;

import java.util.List;

public interface EventServiceAdmin {
    List<EventShortDto> getEventsFilteredBy(AdminEventFilter adminEventFilter
    );

    EventFullDto getEventById(Long id);

    @AssertTrue(message = "Value of user id must be positive")
    default boolean validateUserId(Long userId) {
        if (userId != null)
            return userId > 0;
        return true;
    }

    void patchEvent(@NotNull Event event);
}
