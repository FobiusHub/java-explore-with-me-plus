package ru.practicum.ewm.service.event.repository.filter.open;

import lombok.Builder;
import ru.practicum.ewm.service.event.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OpenEventFilter(
        String text, // by annotation text (case-insensitive);
        List<Long> categories, // by List of category ids;
        Boolean paid, // by paid status (true or false);
        LocalDateTime rangeStart, // by date range (start);
        LocalDateTime rangeEnd, // by date range (end);
        Boolean onlyAvailable, // EventFullDto participantLimit > 0;
        EventSort sort, // sql-request SORT BY value;
        Integer from, // sql-request OFFSET value;
        Integer size // sql-request LIMIT value;
) {
}