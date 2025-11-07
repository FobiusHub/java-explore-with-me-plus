package ru.practicum.ewm.service.event.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceAdmin;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Административный REST контроллер для управления событиями.
 * Предоставляет API для административных операций с событиями с расширенными правами доступа.
 *
 * В отличие от публичного контроллера, позволяет работать с событиями в любом состоянии
 * и использовать расширенные фильтры для поиска.
 *
 * @see EventServiceAdmin
 * @see EventFullDto
 * @see EventShortDto
 */
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdminAPI {

    private final EventServiceAdmin adminEventService;

    /**
     * Получает список событий с применением административных фильтров.
     * Поддерживает расширенные критерии поиска, недоступные в публичном API.
     *
     * @param users      список идентификаторов инициаторов событий
     * @param states     список состояний событий
     * @param categories список идентификаторов категорий
     * @param rangeStart Начальная дата диапазона для поиска событий (по умолчанию - текущее время)
     * @param rangeEnd   Конечная дата диапазона для поиска событий
     * @param from       Количество элементов, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size       Количество элементов в наборе (по умолчанию 10)
     * @return Список событий в кратком формате {@link EventShortDto}
     * @apiNote Все параметры являются обязательными, кроме from и size
     * @example GET /admin/events?users=1,2,3&states=PENDING,PUBLISHED&categories=1,2&rangeStart=2024-01-01T00:00:00&rangeEnd=2024-12-31T23:59:59
     */
    @GetMapping
    public List<EventShortDto> getEventsFilteredBy(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<EventState> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,

            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
            LocalDateTime rangeStart,

            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
            LocalDateTime rangeEnd,

            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size
    ) {
        log.info("GET /admin/events with parameters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={},  from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<Long> validatedUsers = validateIdsList(users);
        List<Long> validatedCategories = validateIdsList(categories);

        List<EventShortDto> responseList = adminEventService.getEventsFilteredBy(AdminEventFilter.builder()
                .users(validatedUsers)
                .states(states)
                .categories(validatedCategories)
                .rangeStart(rangeStart == null ? LocalDateTime.now() : rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build());
        log.info("GET /admin/events, response:{}", responseList);
        return responseList;
    }

    @PatchMapping("/{id}")
    public EventShortDto patchEventById(
            @PathVariable("id") Long id,
            @RequestBody UpdateEventAdminRequest event
    ) {
        log.info("PATCH /events/id with id={}", id);
        event.setId(id);
      return adminEventService.patchEvent(event);
    }

    private List<Long> validateIdsList(List<Long> idsList) {
        if (idsList != null && !idsList.isEmpty()) {
            return idsList.stream().filter(id -> id > 0).toList();
        }
        return new ArrayList<>();
    }
}