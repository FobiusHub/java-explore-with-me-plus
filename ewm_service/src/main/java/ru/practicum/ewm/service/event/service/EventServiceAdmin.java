package ru.practicum.ewm.service.event.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.exception.BadRequestException;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.util.EventMapper;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;
import ru.practicum.ewm.service.event.repository.EventRepository;

import java.util.List;

/**
 * Сервис для административных операций с событиями.
 * Предоставляет функциональность для управления событиями с расширенными правами доступа.
 * В отличие от публичного сервиса, позволяет работать с событиями в любом состоянии.
 *
 * @see EventRepository
 * @see AdminEventFilter
 * @see Event
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventServiceAdmin {

    private final EventRepository eventJpaRepository;

    /**
     * Получает отфильтрованный список событий для административной панели.
     * Поддерживает расширенные фильтры, недоступные в публичном API.
     *
     * @return Список событий в кратком формате {@link EventShortDto}
     * @throws BadRequestException если диапазон дат указан некорректно
     * @apiNote В отличие от публичного API, позволяет фильтровать по пользователям и состояниям
     */
    public List<EventShortDto> getEventsFilteredBy(AdminEventFilter adminEventFilter
    ) {
        return eventJpaRepository.findAllByFilter(adminEventFilter).stream()
                .map(EventMapper::toEventShortDto)
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
        String message = String.format("Event with id=%d was not found", id);
        Event event = eventJpaRepository.findById(id).orElseThrow(() -> new EventNotFoundException(message));

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        log.info("GET /admin/event/{} response:{}", id, eventFullDto);
        return eventFullDto;
    }


    public void patchEvent(@NotNull Event event) {
        eventJpaRepository.save(event);
    }
}