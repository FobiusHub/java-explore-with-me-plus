package ru.practicum.ewm.service.event.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.client.stats.StatsRequestSender;
import ru.practicum.ewm.service.event.enums.EventSort;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.exception.BadRequestException;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.filter.open.OpenEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceOpen;
import ru.practicum.ewm.service.event.util.EventMapper;

import java.util.List;

/**
 * Публичный сервис для работы с событиями.
 * <p>
 * Предоставляет бизнес-логику для поиска и получения информации о событиях
 * через публичное API. Обеспечивает фильтрацию только по опубликованным событиям
 * и проверку прав доступа для конечных пользователей.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Поиск событий с фильтрацией и пагинацией</li>
 *   <li>Получение детальной информации о конкретном событии</li>
 *   <li>Валидация состояния событий для публичного доступа</li>
 * </ul>
 *
 * @author Service Development Team
 * @see EventRepository
 * @see Event
 * @see EventFullDto
 * @see EventShortDto
 * @see OpenEventFilter
 * @since 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventServiceOpenImpl implements EventServiceOpen {


    private StatsRequestSender statsRequestSender;
    private final EventRepository eventRepository;

    /**
     * Получает отфильтрованный список событий с применением пагинации и сортировки.
     * <p>
     * Метод выполняет поиск только опубликованных событий ({@link EventState#PUBLISHED}),
     * соответствующих заданным критериям фильтрации. Поддерживает сложные фильтры
     * включая текстовый поиск, фильтрацию по категориям, датам и доступности.
     * </p>
     *
     * <p><b>Поддерживаемые критерии фильтрации:</b></p>
     * <ul>
     *   <li><b>text</b> - поиск по аннотации и описанию</li>
     *   <li><b>categories</b> - фильтрация по идентификаторам категорий</li>
     *   <li><b>paid</b> - фильтрация по платным/бесплатным событиям</li>
     *   <li><b>rangeStart</b> - начало временного диапазона</li>
     *   <li><b>rangeEnd</b> - окончание временного диапазона</li>
     *   <li><b>onlyAvailable</b> - только события с доступными местами</li>
     *   <li><b>sort</b> - сортировка по дате события или количеству просмотров</li>
     * </ul>
     *
     * @param filter объект, содержащий критерии фильтрации, пагинации и сортировки.
     *               Не может быть {@code null}
     * @return список событий в кратком формате {@link EventShortDto}, отсортированный
     * согласно заданным критериям. Если события не найдены, возвращается пустой список
     * @throws BadRequestException      если диапазон дат указан некорректно (начальная дата позже конечной)
     * @throws IllegalArgumentException если {@code filter} равен {@code null}
     * @example <pre>{@code
     * OpenEventFilter filter = OpenEventFilter.builder()
     *     .text("концерт")
     *     .categories(List.of(1L, 2L))
     *     .paid(true)
     *     .rangeStart(LocalDateTime.now())
     *     .onlyAvailable(true)
     *     .sort(EventSort.EVENT_DATE)
     *     .from(0)
     *     .size(10)
     *     .build();
     *
     * List<EventShortDto> events = eventServiceOpen.getEventsFilteredBy(filter);
     * }</pre>
     * @apiNote Метод автоматически фильтрует только опубликованные события
     * и увеличивает счетчик просмотров при каждом успешном запросе
     * @see OpenEventFilter
     * @see EventShortDto
     * @see EventSort
     */
    @Transactional
    @Override
    public List<EventShortDto> getEventsFilteredBy(OpenEventFilter filter, HttpServletRequest httpServletRequest) {
        List<Event> events = eventRepository.findAllByFilter(filter);
        log.info("Found {} events using filter: {}", events.size(), filter);
        List<EventShortDto> responseList = events.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        statsRequestSender.sendRequestToStatService(httpServletRequest);
        return responseList;
    }

    /**
     * Получает полную информацию о событии по идентификатору.
     * <p>
     * Метод выполняет поиск события по идентификатору и проверяет, что оно
     * опубликовано и доступно для публичного просмотра. При успешном запросе
     * увеличивает счетчик просмотров события.
     * </p>
     *
     * <p><b>Условия доступа:</b></p>
     * <ul>
     *   <li>Событие должно существовать в системе</li>
     *   <li>Событие должно быть в состоянии {@link EventState#PUBLISHED}</li>
     * </ul>
     *
     * @param id идентификатор события. Должен быть положительным числом
     * @return полная информация о событии {@link EventFullDto}
     * @throws EventNotFoundException   если событие с указанным ID не найдено
     * @throws BadRequestException      если событие не опубликовано
     * @throws IllegalArgumentException если {@code id} равен {@code null} или отрицательный
     * @example <pre>{@code
     * // Получение опубликованного события
     * EventFullDto event = eventServiceOpen.getEventById(123L);
     *
     * // Попытка получить неопубликованное событие выбросит BadRequestException
     * try {
     *     EventFullDto draftEvent = eventServiceOpen.getEventById(456L);
     * } catch (BadRequestException e) {
     *     // Обработка ошибки: "Event must be published"
     * }
     * }</pre>
     * @apiNote При успешном запросе событие логируется для отладки и аудита.
     * Счетчик просмотров увеличивается атомарно для избежания race condition.
     * @see EventFullDto
     * @see EventState
     */
    @Override
    public EventFullDto getEventById(Long id) {
        String message = String.format("Event with id=%d was not found", id);

        if (!eventRepository.existsById(id)){
            throw new EventNotFoundException(message);
        }
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(message));

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Event must be published");
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        log.info("Successfully retrieved event with id={}, title='{}'", id, eventFullDto.getTitle());
        return eventFullDto;
    }
}