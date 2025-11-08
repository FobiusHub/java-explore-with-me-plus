package ru.practicum.ewm.service.event.repository.filter.internal;

import lombok.Builder;

@Builder
public record InternalEventFilter(
        Long userId,
        Long eventId,
        Integer from,
        Integer size
) {
};