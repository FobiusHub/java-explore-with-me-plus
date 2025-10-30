package ru.practicum.ewm.service.event.repository.EventFilterAdmin;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AdminEventFilter(
        List<Long> users,
        List<String> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Integer from,
        Integer size
) {
};


