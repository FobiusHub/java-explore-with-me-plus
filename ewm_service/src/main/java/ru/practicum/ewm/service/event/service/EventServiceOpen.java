package ru.practicum.ewm.service.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.enums.EventSort;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.error.BadRequestException;
import ru.practicum.ewm.service.event.error.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.EventJpaRepository;
import ru.practicum.ewm.service.event.repository.filter.open.OpenEventFilter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Публичный сервис для работы с событиями.
 * Предоставляет бизнес-логику для поиска и получения информации о событиях.
 * Обеспечивает фильтрацию только по опубликованным событиям.
 *
 * @see EventJpaRepository
 * @see Event
 * @see EventFullDto
 * @see EventShortDto
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventServiceOpen {

    private final EventJpaRepository eventJpaRepository;

    /**
     * Получает отфильтрованный список событий с применением пагинации и сортировки.
     * Все события проходят проверку на публикацию и доступность.
     *
     * @param text          Текст для поиска в аннотации и описании событий (регистронезависимый)
     * @param categories    Список идентификаторов категорий для фильтрации
     * @param paid          Фильтр по платным/бесплатным событиям
     * @param rangeStart    Начальная дата диапазона событий
     * @param rangeEnd      Конечная дата диапазона событий
     * @param onlyAvailable Флаг показа только доступных событий (имеющих свободные места)
     * @param sort          Тип сортировки результатов
     * @param from          Начальная позиция пагинации
     * @param size          Количество элементов на странице
     * @return Список событий в кратком формате {@link EventShortDto}
     * @throws BadRequestException если диапазон дат указан некорректно
     * @apiNote Метод автоматически фильтрует только опубликованные события
     * @see OpenEventFilter
     */
    public List<EventShortDto> getEventsFilteredBy(String text, // by annotation text (case-insensitive);
                                                   List<Long> categories, // by List of category ids;
                                                   Boolean paid, // by paid status (true or false);
                                                   LocalDateTime rangeStart, // by date range (start);
                                                   LocalDateTime rangeEnd, // by date range (end);
                                                   Boolean onlyAvailable, // EventFullDto participantLimit > 0;
                                                   EventSort sort, // sql-request SORT BY value;
                                                   Integer from, // sql-request OFFSET value;
                                                   Integer size // sql-request LIMIT value;
    ) {
        validateEventDateRange(rangeStart, rangeEnd);

        OpenEventFilter filter = OpenEventFilter.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        List<Event> events = eventJpaRepository.findAllByFilter(filter);
        log.debug("Found {} events using filter: {}", events.size(), filter);

        return events.stream().map(Event::toEventShortDto).toList();
    }

    /**
     * Получает полную информацию о событии по идентификатору.
     * Проверяет, что событие опубликовано и доступно для просмотра.
     *
     * @param id Идентификатор события
     * @return Полная информация о событии {@link EventFullDto}
     * @throws EventNotFoundException если событие с указанным ID не найдено
     * @throws BadRequestException    если событие не опубликовано
     * @apiNote При успешном запросе событие логируется для отладки
     */
    public EventFullDto getEventById(Long id) {
        String notFoundMessage = String.format("Event with id=%d was not found", id);
        Event event = eventJpaRepository.findById(id).orElseThrow(() -> new EventNotFoundException(notFoundMessage));

        if (event.getState() != EventState.PUBLISHED) {
            String stateInfo = event.getState() != null ? event.getState().toString() : "null";
            String errorMessage = String.format("Event with id=%d must be published. Current state: %s", id, stateInfo);
            log.warn(errorMessage);
            throw new BadRequestException("Event must be published");
        }

        EventFullDto eventFullDto = event.toEventFullDto();
        log.info("Successfully retrieved event with id={}, title='{}'", id, eventFullDto.getTitle());
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
            String errorMessage = String.format("Invalid date range: rangeStart (%s) must be before rangeEnd (%s)", rangeStart, rangeEnd);
            log.warn(errorMessage);
            throw new BadRequestException("RangeStart must be before RangeEnd");
        }
    }
}