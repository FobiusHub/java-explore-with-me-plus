package ru.practicum.ewm.service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.enums.EventSort;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.filter.open.OpenEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceOpen;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Публичный REST контроллер для работы с событиями.
 * Предоставляет API для поиска и просмотра событий без аутентификации.
 *
 * @see EventServiceOpen
 * @see EventFullDto
 * @see EventShortDto
 */
@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventControllerOpenAPI {

    private final EventServiceOpen eventServiceOpen;

    /**
     * Получает список событий с применением фильтров и пагинации.
     * Поиск осуществляется только по опубликованным событиям.
     *
     * @param text          Текст для поиска в аннотации и описании событий (без учета регистра)
     * @param categories    Список идентификаторов категорий для фильтрации
     * @param paid          Фильтр по платным/бесплатным событиям
     * @param rangeStart    Начальная дата диапазона для поиска событий (по умолчанию - текущее время)
     * @param rangeEnd      Конечная дата диапазона для поиска событий
     * @param onlyAvailable Флаг показа только доступных событий (свободные места > 0)
     * @param sort          Тип сортировки результатов (по дате или по просмотрам)
     * @param from          Количество элементов, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size          Количество элементов в наборе (по умолчанию 10)
     * @return Список событий в кратком формате {@link EventShortDto}, соответствующих критериям поиска
     * @apiNote Все параметры, кроме from и size, являются необязательными
     * @example GET /events?text=концерт&categories=1,2&paid=true&onlyAvailable=true&sort=EVENT_DATE
     */
    @GetMapping
    public List<EventShortDto> getEventsFilteredBy(
            @RequestParam(value = "text", required = false)
            String text,

            @RequestParam(value = "categories", required = false)
            List<Long> categories,

            @RequestParam(value = "paid", required = false)
            Boolean paid,

            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
            LocalDateTime rangeStart,

            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
            LocalDateTime rangeEnd,

            @RequestParam(value = "onlyAvailable", required = false)
            Boolean onlyAvailable,

            @RequestParam(value = "sort", required = false)
            EventSort sort,

            @PositiveOrZero
            @RequestParam(value = "from", defaultValue = "0")
            Integer from,

            @Positive
            @RequestParam(value = "size", defaultValue = "10")
            Integer size,

            HttpServletRequest httpServletRequest
    ) {
        log.info("GET /events with parameters: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}," +
                        "onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<Long> validatedCategories = validateListOfIds(categories);

        OpenEventFilter openEventFilter = OpenEventFilter.builder()
                .text(text)
                .categories(validatedCategories)
                .paid(paid)
                .rangeStart(rangeStart == null ? LocalDateTime.now() : rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        List<EventShortDto> responseList = eventServiceOpen.getEventsFilteredBy(openEventFilter, httpServletRequest);
        log.info("GET /events response: found {} events", responseList.size());
        return responseList;
    }

    /**
     * Получает полную информацию о событии по его идентификатору.
     * Возвращает информацию только об опубликованных событиях.
     *
     * @param id Идентификатор события (обязательный параметр)
     * @return Полная информация о событии {@link EventFullDto}
     * @throws ru.practicum.ewm.service.event.exception.EventNotFoundException если событие не найдено
     * @throws ru.practicum.ewm.service.event.exception.BadRequestException    если событие не опубликовано
     * @apiNote При каждом успешном запросе увеличивает счетчик просмотров события
     * @example GET /events/14
     */
    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable("id") @Positive Long id, HttpServletRequest request
    ) {
        log.info("GET /events/{}", id);
        EventFullDto event = eventServiceOpen.getEventById(id, request);
        log.info("GET /events/{} response: event '{}'", id, event.getTitle());
        return event;
    }

    private List<Long> validateListOfIds(List<Long> idsList) {
        if (idsList != null && !idsList.isEmpty()) {
            return idsList.stream().filter(id -> id >= 0).toList();
        }
        return new ArrayList<>();
    }
}