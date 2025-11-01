package ru.practicum.ewm.service.event.repository.filter.admin;

import jakarta.validation.constraints.AssertTrue;
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
    @AssertTrue(message = "RangeStart must be before RangeEnd")
    private boolean isValidDateRange() {
        if (rangeStart != null && rangeEnd != null) {
            return rangeStart.isBefore(rangeEnd);
        }
        return true;
    }
}


