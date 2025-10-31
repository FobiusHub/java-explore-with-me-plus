package ru.practicum.ewm.service.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.error.BadRequestException;
import ru.practicum.ewm.service.event.error.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;
import ru.practicum.ewm.service.event.repository.EventJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для административных операций с событиями.
 * Предоставляет функциональность для управления событиями с расширенными правами доступа.
 * В отличие от публичного сервиса, позволяет работать с событиями в любом состоянии.
 *
 * @see EventJpaRepository
 * @see AdminEventFilter
 * @see Event
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventServiceAdmin {

    private final EventJpaRepository eventJpaRepository;

    /**
     * Получает отфильтрованный список событий для административной панели.
     * Поддерживает расширенные фильтры, недоступные в публичном API.
     *
     * @param users      Список идентификаторов инициаторов событий для фильтрации
     * @param states     Список состояний событий для фильтрации
     * @param categories Список идентификаторов категорий для фильтрации
     * @param rangeStart Начальная дата диапазона событий
     * @param rangeEnd   Конечная дата диапазона событий
     * @param from       Начальная позиция пагинации
     * @param size       Количество элементов на странице
     * @return Список событий в кратком формате {@link EventShortDto}
     * @throws BadRequestException если диапазон дат указан некорректно
     * @apiNote В отличие от публичного API, позволяет фильтровать по пользователям и состояниям
     */
    public List<EventShortDto> getEventsFilteredBy(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        validateEventDateRange(rangeStart, rangeEnd);
        return eventJpaRepository.findAllByFilter(AdminEventFilter.builder()
                        .users(users)
                        .states(states)
                        .categories(categories)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .from(from)
                        .size(size)
                        .build()).stream()
                .map(Event::toEventShortDto)
                .toList();
    }

    /**
     * Получает полную информацию о событии по идентификатору для административных целей.
     * В отличие от публичного API, возвращает события в любом состоянии.
     *
     * @param id Идентификатор события
     * @return Полная информация о событии {@link EventFullDto}
     * @throws EventNotFoundException если событие с указанным ID не найдено
     * @apiNote Возвращает события в любом состоянии (включая неопубликованные)
     */
    public EventFullDto getEventById(Long id) {
        String messageENFE = String.format("Event with id=%d was not found", id);
        Event event = eventJpaRepository.findById(id).orElseThrow(() -> new EventNotFoundException(messageENFE));

        EventFullDto eventFullDto = event.toEventFullDto();
        log.info("GET /admin/event/{} response:{}", id, eventFullDto);
        return eventFullDto;
    }

    /**
     * Проверяет корректность диапазона дат для фильтрации событий.
     *
     * @param rangeStart Начальная дата диапазона
     * @param rangeEnd   Конечная дата диапазона
     * @throws BadRequestException если начальная дата позже конечной даты
     */
    private void validateEventDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("RangeStart must be before RangeEnd");
        }
    }
}