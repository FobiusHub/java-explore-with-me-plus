package ru.practicum.ewm.service.event.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.UpdateEventDto;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.enums.StateAction;
import ru.practicum.ewm.service.event.exception.BadRequestException;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceAdmin;
import ru.practicum.ewm.service.event.util.EventMapper;

import java.time.LocalDateTime;
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
public class EventServiceAdminImpl implements EventServiceAdmin {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Получает отфильтрованный список событий для административной панели.
     * Поддерживает расширенные фильтры, недоступные в публичном API.
     *
     * @return Список событий в кратком формате {@link EventShortDto}
     * @throws BadRequestException если диапазон дат указан некорректно
     * @apiNote В отличие от публичного API, позволяет фильтровать по пользователям и состояниям
     */
    @Override
    public List<EventShortDto> getEventsFilteredBy(AdminEventFilter adminEventFilter) {

        return eventRepository.findAllByFilter(adminEventFilter).stream().map(EventMapper::toEventShortDto).toList();
    }

    @Override
    public void patchEvent(UpdateEventDto eventToUpdate) {
        Long eventId = eventToUpdate.getId();

        Event currentEvent = getEvent(eventId);

        /*
         * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.
         * (Ожидается код ошибки 409)
         */
        if (eventToUpdate.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (currentEvent.getState() != EventState.PENDING) {
                String message = "Event must be in PENDING state to be published";
                throw new BadRequestException(message);
            }
        }

        /*
         * событие можно публиковать, только если оно в состоянии ожидания публикации
         * (Ожидается код ошибки 409)
         */
        if (eventToUpdate.getStateAction() == StateAction.CANCEL_EVENT) {
            if (currentEvent.getState() == EventState.PUBLISHED) {
                String message = "Event must be in PENDING state to be published";
                throw new BadRequestException(message);
            }
        }

        /*
         * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.
         * (Ожидается код ошибки 409)
         */
        currentEvent.setPublishedOn(LocalDateTime.now());
        LocalDateTime publishedOn = currentEvent.getPublishedOn();
        long HOURS_RANGE_BETWEEN_PUBLISHING_AND_EVENTDATE = 1L;
        if (currentEvent.getEventDate().isBefore(publishedOn.plusHours(HOURS_RANGE_BETWEEN_PUBLISHING_AND_EVENTDATE))) {
            String message = "Event date must be at least one hour after the publication date";
            throw new BadRequestException(message);
        }

        if (eventToUpdate.getEventDate() != null) {
            LocalDateTime eventDate = eventToUpdate.getEventDate();
            currentEvent.setEventDate(eventDate);
        }

        if (eventToUpdate.getAnnotation() != null) {
            String annotation = eventToUpdate.getAnnotation();
            currentEvent.setAnnotation(annotation);
        }

        if (eventToUpdate.getCategory() != null) {
            Long newCategoryId = eventToUpdate.getCategory();
            Long currentCategoryId = currentEvent.getCategory() == null ?
                    null : currentEvent.getCategory().getId();
            if (!newCategoryId.equals(currentCategoryId)) {
                Category newCategory = getCategory(newCategoryId);
                currentEvent.setCategory(newCategory);
            }
        }

        if (eventToUpdate.getDescription() != null) {
            String description = eventToUpdate.getDescription();
            currentEvent.setDescription(description);
        }

        if (eventToUpdate.getLocation() != null) {
            Location location = eventToUpdate.getLocation();
            currentEvent.setLocation(location);
        }

        if (eventToUpdate.getPaid() != null) {
            Boolean paid = eventToUpdate.getPaid();
            currentEvent.setPaid(paid);
        }

        if (eventToUpdate.getParticipantLimit() != null) {
            Integer participantLimit = eventToUpdate.getParticipantLimit();
            currentEvent.setParticipantLimit(participantLimit);
        }

        if (eventToUpdate.getRequestModeration() != null) {
            Boolean requestModeration = eventToUpdate.getRequestModeration();
            currentEvent.setRequestModeration(requestModeration);
        }

        if (eventToUpdate.getRequestModeration() != null) {
            Boolean requestModeration = eventToUpdate.getRequestModeration();
            currentEvent.setRequestModeration(requestModeration);
        }

        if (eventToUpdate.getTitle() != null) {
            String title = eventToUpdate.getTitle();
            currentEvent.setTitle(title);
        }

        eventRepository.save(currentEvent);
        log.info("Event id={} has been updated", eventId);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("При запросе категории возникла ошибка: категория не найдена");
            return new NotFoundException("Категория " + id + " не найдена");
        });
    }

    private Event getEvent(Long id) {
        String message = String.format("Event with id=%d was not found", id);
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(message));
    }
}