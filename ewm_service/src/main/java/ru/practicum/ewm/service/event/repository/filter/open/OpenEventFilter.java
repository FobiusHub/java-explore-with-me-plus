package ru.practicum.ewm.service.event.repository.filter.open;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.service.event.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OpenEventFilter {
    private String text; // by annotation text (case-insensitive);
    private List<Long> categories; // by List of category ids;
    private Boolean paid; // by paid status (true or false);
    private LocalDateTime rangeStart; // by date range (start);
    private LocalDateTime rangeEnd; // by date range (end);
    private Boolean onlyAvailable; // EventFullDto participantLimit > 0;
    private EventSort sort; // sql-request SORT BY value;
    private Integer from; // sql-request OFFSET value;
    private Integer size; // sql-request LIMIT value;

    @AssertTrue(message = "RangeStart must be before RangeEnd")
    private boolean isValidDateRange() {
        if (rangeStart != null && rangeEnd != null) {
            return rangeStart.isBefore(rangeEnd);
        }
        return true;
    }
}

