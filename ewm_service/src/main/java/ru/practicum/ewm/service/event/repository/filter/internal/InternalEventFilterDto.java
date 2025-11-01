package ru.practicum.ewm.service.event.repository.filter.internal;

import lombok.Builder;

@Builder
public record InternalEventFilterDto(
        Long userId,
        Integer from,
        Integer size
) {
};


