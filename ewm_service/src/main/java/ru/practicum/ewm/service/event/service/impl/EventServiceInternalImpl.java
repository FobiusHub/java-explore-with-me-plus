package ru.practicum.ewm.service.event.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.dto.UpdateRequestDto;
import ru.practicum.ewm.service.event.exception.BadRequestException;
import ru.practicum.ewm.service.event.exception.EventNotFoundException;
import ru.practicum.ewm.service.event.model.Event;
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

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceInternalImpl implements EventServiceInternal {

    private final EventBuilder eventBuilder;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getEventsOfUserBy(InternalEventFilter filter) {
        return eventRepository.findByFilter(filter).stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto postEvent(NewEventDto newEventDto) {
        Long userId = newEventDto.getInitiatorId();
        Event eventToSave = eventBuilder.buildEventBy(newEventDto);
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
    public void updateEventStatus(UpdateRequestDto updateRequestDto, Long userId, Long eventId) {

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
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event id=%d not found", eventId)));

        // проверка, что у события неограниченный лимит участников (модерация не требуется)
        if (event.getParticipantLimit() != null) {
            if (event.getParticipantLimit() == 0) {
                log.info("Request conformation is not required. Participant limit: 0 (unlimited)");
                return;
            }
        }

        // проверка, что пользователь является инициатором события
        if (event.getInitiator() != null) {
            User initiator = event.getInitiator();
            Long initiatorId = initiator.getId();
            if (!initiatorId.equals(userId)) {
                String message = String.format("User id=%d is not initiator of event id=%d", userId, eventId);
                throw new BadRequestException(message);
            }
        }

        // проверка по флагу, что событие требует модерации
        if (event.getRequestModeration() != null) {
            if (!event.getRequestModeration()) {
                log.info("Request conformation is not required. Request moderation of event: false");
                return;
            }
        }

        // проверка, что у события не исчерпан лимит участников
        if (event.getParticipantLimit() != null) {
            if (event.getConfirmedParticipantRequests() != null) {
                int limit = event.getParticipantLimit();
                int value = event.getConfirmedParticipantRequests();
                if (value >= limit) {
                    String message = String.format("Unable to confirm request. Participant limit of requests: %d. " +
                            "Confirmed requests: %d", limit, value);
                    throw new BadRequestException(message);
                }
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
            throw new BadRequestException(message); // или return - пока хз по тз
        }

        // если хотя бы у одного из полученных запросов на участие статус не PENDING
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                String message = String.format("Extended request status: %s, current request id=%d status: %s",
                        RequestStatus.PENDING, eventId, request.getStatus());
                throw new BadRequestException(message);
            }
        }

        // проверка, что после подтверждения всех запросов на участие не будет превышен лимит участников события
        Integer limit = event.getParticipantLimit();
        Integer confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);
        int countOfAllowedNumbers = limit - confirmedRequests;
        if (requests.size() > countOfAllowedNumbers) {
            String message = String.format("The participant limit=%d has been reached", limit);
            throw new BadRequestException(message);
        }

        // новый статус запроса на участие
        RequestStatus newStatus = updateRequestDto.getStatus();

        // обновление статуса запросов на участие
        requests.forEach(r -> r.setStatus(newStatus));

        // запись запросов на участие в БД
        requestRepository.saveAll(requests);
    }

    private ParticipationRequest getRequestBy(Long userId, Long eventId) {
        return requestRepository.findByRequesterIdAndEventId(userId, eventId);
    }
}