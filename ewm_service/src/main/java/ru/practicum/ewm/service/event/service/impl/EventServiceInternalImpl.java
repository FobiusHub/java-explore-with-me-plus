package ru.practicum.ewm.service.event.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.common.exception.ConflictException;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.enums.StateActionInternal;
import ru.practicum.ewm.service.event.exception.BadRequestException;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceInternal;
import ru.practicum.ewm.service.event.util.EventBuilder;
import ru.practicum.ewm.service.event.util.EventMapper;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.mapper.RequestMapper;
import ru.practicum.ewm.service.request.model.ParticipationRequest;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.request.repository.RequestRepository;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceInternalImpl implements EventServiceInternal {

    private final EventBuilder eventBuilder;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter) {
        return eventRepository.findByFilter(filter).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto postEvent(NewEventDto newEventDto, Long userId) {
        Event eventToSave = eventBuilder.buildEventBy(newEventDto, userId);
        Event savedEvent = eventRepository.save(eventToSave);
        log.info("GET /users/{}/events response:{}", userId, savedEvent);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventOfUserBy(InternalEventFilter filter) {
        if (!userRepository.existsById(filter.userId())) {
            String message = String.format("User with id=%d is not exists", filter.userId());
            throw new NotFoundException(message);
        }
        String message = String.format("Event with id=%d not found", filter.eventId());
        List<Event> events = eventRepository.findByFilter(filter);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException(message));
    }

    @Override
    public ParticipationRequestDto getRequestOfUserBy(Long userId, Long eventId) {
        ParticipationRequest request = getRequestBy(userId, eventId);
        return RequestMapper.toRequestDto(request);
    }

    @Transactional
    @Override
    public void updateRequestStatus(UpdateRequestDto updateRequestDto, Long userId, Long eventId) {

        // проверка, что запрос на обновление статуса запроса - CONFIRMED
        if (updateRequestDto.getStatus() != RequestStatus.CONFIRMED) {
            String message = String.format("Required request status: %s. Current request status: %s",
                    RequestStatus.CONFIRMED, updateRequestDto.getStatus());
            throw new BadRequestException(message);
        }

        // проверка на наличие пользователя (инициатора события) в БД
        if (!userRepository.existsById(userId)) {
            String message = String.format("User id=%d not found", userId);
            log.warn(message);
            throw new NotFoundException(message);
        }

        // поиск в БД события по его id
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event id=%d not found", eventId)));


        // проверка, что запрос на обновление статуса запроса - CONFIRMED
        if (currentEvent.getStatus() != EventState.PUBLISHED) {
            String message = String.format("Required event status: %s. Current request status: %s",
                    EventState.PUBLISHED, currentEvent.getStatus());
            throw new ConflictException(message);
        }

        // проверка, что у события неограниченный лимит участников (модерация не требуется)
        if (currentEvent.getParticipantLimit() != null) {
            if (currentEvent.getParticipantLimit() == 0) {
                log.info("Request conformation is not required. Participant limit: 0 (unlimited)");
                return;
            }
        }

        // проверка, что пользователь является инициатором события
        if (currentEvent.getInitiator() != null) {
            User initiator = currentEvent.getInitiator();
            Long initiatorId = initiator.getId();
            if (!initiatorId.equals(userId)) {
                String message = String.format("User id=%d is not initiator of event id=%d", userId, eventId);
                throw new BadRequestException(message);
            }
        }

        // проверка по флагу, что событие требует модерации
        if (currentEvent.getRequestModeration() != null) {
            if (!currentEvent.getRequestModeration()) {
                log.info("Request conformation is not required. Request moderation of event: false");
                return;
            }
        }

        // проверка, что у события не исчерпан лимит участников
        Integer limit = currentEvent.getParticipantLimit();
        Long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (limit > 0) { // limit = 0 means unlimited
            if (confirmedRequests >= limit) {
                String message = String.format("Limit value of requests is reached. " +
                        "Current limit: %d, confirmed requests: %d", limit, confirmedRequests);
                log.warn(message);
                throw new ConflictException(message);
            }
        }

        // фильтрация положительных id-запросов на участие
        List<Long> requestIds = updateRequestDto.getRequestIds().stream()
                .filter(id -> id > 0)
                .toList();

        // получение списка запросов на участие
        List<ParticipationRequest> requests = requestRepository.findByIdInAndEventId(requestIds, eventId);

        // если по списку id-запросов не участия не найдено ни одного объекта
        if (requests.isEmpty()) {
            String message = "There are no elements (participant requests) satisfying the request";
            throw new ConflictException(message); // или return - пока хз по тз
        }

        // если хотя бы у одного из полученных запросов на участие статус не PENDING
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                String message = String.format("Extended request status: %s, current request id=%d status: %s",
                        RequestStatus.PENDING, eventId, request.getStatus());
                throw new ConflictException(message);
            }
        }

        // проверка, что после подтверждения всех запросов на участие не будет превышен лимит участников события
        limit = currentEvent.getParticipantLimit();
        confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);
        long countOfAllowedNumbers = limit - confirmedRequests;
        if (requests.size() > countOfAllowedNumbers) {
            String message = String.format("The participant limit=%d has been reached", limit);
            throw new ConflictException(message);
        }

        // новый статус запроса на участие
        RequestStatus newStatus = updateRequestDto.getStatus();

        // обновление статуса запросов на участие
        requests.forEach(r -> r.setStatus(newStatus));

        // запись запросов на участие в БД
        requestRepository.saveAll(requests);
    }

    @Override
    public EventFullDto patchEventOfUserBy(UpdateEventUserRequest updateEvent, Long userId, Long eventId) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event id=%d not found", eventId)));

        if (userId != null && !currentEvent.getInitiator().getId().equals(userId)) {
            String message = String.format("User id=%d is not initiator of event id=%d", userId, eventId);
            throw new BadRequestException(message);
        }

        if (currentEvent.getStatus() == EventState.PUBLISHED) {
            String message = "";
            log.warn(message);
            throw new ConflictException(message);
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

        LocalDateTime eventDate = updateEvent.getEventDate() == null ?
                currentEvent.getEventDate() : updateEvent.getEventDate();
        currentEvent.setEventDate(eventDate);

        Location location = updateEvent.getLocation() == null ?
                currentEvent.getLocation() : updateEvent.getLocation();
        currentEvent.setLocation(location);

        Boolean paid = updateEvent.getPaid() == null ?
                currentEvent.getPaid() : updateEvent.getPaid();
        currentEvent.setPaid(paid);

        Integer participantLimit = updateEvent.getParticipantLimit() == null ?
                currentEvent.getParticipantLimit() : updateEvent.getParticipantLimit();
        currentEvent.setParticipantLimit(participantLimit);

        Boolean requestModeration = updateEvent.getRequestModeration() == null ?
                currentEvent.getRequestModeration() : updateEvent.getRequestModeration();
        currentEvent.setRequestModeration(requestModeration);

        if (updateEvent.getStateAction() != null) {
            StateActionInternal stateAction = updateEvent.getStateAction();
            if (stateAction == StateActionInternal.CANCEL_REVIEW) {
                currentEvent.setStatus(EventState.CANCELED);
            }
            if (stateAction == StateActionInternal.SEND_TO_REVIEW) {
                currentEvent.setStatus(EventState.PENDING);
            }
        }

        String title = updateEvent.getTitle() == null ?
                currentEvent.getTitle() : updateEvent.getTitle();
        currentEvent.setTitle(title);

        Event event = eventRepository.save(currentEvent);

        return EventMapper.toEventFullDto(event);
    }

    private ParticipationRequest getRequestBy(Long userId, Long eventId) {
        return requestRepository.findByRequesterIdAndEventId(userId, eventId);
    }
}