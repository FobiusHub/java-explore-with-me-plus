package ru.practicum.ewm.service.event.repository.filter.admin;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.service.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminEventFilter {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    @AssertTrue(message = "RangeStart must be before RangeEnd")
    private boolean isValidDateRange() {
        if (rangeStart != null && rangeEnd != null) {
            return rangeStart.isBefore(rangeEnd);
        }
        return true;
    }
}


