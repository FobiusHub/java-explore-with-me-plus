package ru.practicum.ewm.service.event.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.enums.StateActionAdmin;
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
    public EventShortDto patchEvent(UpdateEventAdminRequest updateEvent, Long eventId) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event id=%d not found", eventId)));

        if (updateEvent.getStateAction() != null) {
            /*
             * обновление статуса текущего события
             */
            StateActionAdmin stateAction = updateEvent.getStateAction();
            switch (stateAction) {
                /*
                 * событие можно отклонить, только если оно еще не опубликовано
                 * (Ожидается код ошибки 409)
                 */
                case StateActionAdmin.CANCEL_EVENT:
                    if (currentEvent.getStatus() == EventState.PUBLISHED) {
                        String message = String.format("Cannot cancel event. Event status: %s", EventState.PUBLISHED);
                        throw new BadRequestException(message);
                    }
                    currentEvent.setStatus(EventState.CANCELED);
                    break;

                /*
                 * событие можно публиковать, только если оно в состоянии ожидания публикации
                 * (Ожидается код ошибки 409)
                 */
                case StateActionAdmin.PUBLISH_EVENT:
                    if (currentEvent.getStatus() != EventState.PENDING) {
                        String message = String.format("Event must be in PENDING state. " +
                                "Current event state: %s", currentEvent.getStatus());
                        throw new BadRequestException(message);
                    }
                    LocalDateTime currentDate = LocalDateTime.now();
                    LocalDateTime dateEvent = currentEvent.getEventDate();
                    long timeBufferUntilPublicationDate = 1;
                    if (currentDate.isAfter(dateEvent.minusHours(timeBufferUntilPublicationDate))) {
                        String message = String.format("The event start time must be scheduled no earlier than" +
                                "%d hour(s) after publication", timeBufferUntilPublicationDate);
                        throw new BadRequestException(message);
                    }
                    currentEvent.setStatus(EventState.PUBLISHED);
                    currentEvent.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }

        /*
         * Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.
         * (Ожидается код ошибки 409)
         */
        currentEvent.setPublishedOn(LocalDateTime.now());
        LocalDateTime publishedOn = currentEvent.getPublishedOn();
        long hoursRangeBetweenPublishingAndEventDate = 1L;
        if (currentEvent.getEventDate().isBefore(publishedOn.plusHours(hoursRangeBetweenPublishingAndEventDate))) {
            String message = "Event date must be at least one hour after the publication date";
            throw new BadRequestException(message);
        }

        String annotation = updateEvent.getAnnotation() == null ?
                currentEvent.getAnnotation() : updateEvent.getAnnotation();
        currentEvent.setAnnotation(annotation);

        if (updateEvent.getCategory() != null) {
            if (!updateEvent.getCategory().equals(currentEvent.getCategory().getId())) {
                Long categoryId = updateEvent.getCategory();
                Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
                    String message = String.format("Unable to get category id=%d", categoryId);
                    log.warn(message);
                    return new NotFoundException(message);
                });
                currentEvent.setCategory(category);
            }
        }

        String description = updateEvent.getDescription() == null ?
                currentEvent.getDescription() : updateEvent.getDescription();
        currentEvent.setDescription(description);

        Location location = updateEvent.getLocation() == null ?
                currentEvent.getLocation() : updateEvent.getLocation();
        currentEvent.setLocation(location);

        boolean paid = updateEvent.getPaid();
        currentEvent.setPaid(paid);

        Integer participantLimit = updateEvent.getParticipantLimit() == null ?
                currentEvent.getParticipantLimit() : updateEvent.getParticipantLimit();
        currentEvent.setParticipantLimit(participantLimit);

        Boolean requestModeration = updateEvent.getRequestModeration() == null ?
                currentEvent.getRequestModeration() : updateEvent.getRequestModeration();
        currentEvent.setRequestModeration(requestModeration);

        String title = updateEvent.getTitle() == null ?
                currentEvent.getTitle() : updateEvent.getTitle();
        currentEvent.setTitle(title);

        Event eventResponse = eventRepository.save(currentEvent);
        log.info("Event id={} has been updated", eventId);

        return EventMapper.toEventShortDto(eventResponse);
    }
}