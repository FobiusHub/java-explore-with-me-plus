package ru.practicum.ewm.service.event.repository.filter.admin;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.ewm.service.event.error.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public record AdminEventFilterDto(
        List<Long> users,
        List<String> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        Integer from,
        Integer size
) {
    /**
     * Проверяет корректность диапазона дат для фильтрации событий.
     *
     * @param rangeStart Начальная дата диапазона
     * @param rangeEnd   Конечная дата диапазона
     * @throws BadRequestException если начальная дата позже конечной даты
     */
    @AssertTrue(message = "RangeStart must be before RangeEnd")
    private void validateEventDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("RangeStart must be before RangeEnd");
        }
    }

    public AdminEventFilterDto toAdminEventFilter() {
        return AdminEventFilterDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
    }
};


