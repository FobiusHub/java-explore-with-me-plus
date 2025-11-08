package ru.practicum.ewm.service.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.common.exception.ValidationException;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.mapper.RequestMapper;
import ru.practicum.ewm.service.request.model.ParticipationRequest;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.request.repository.RequestRepository;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;
import ru.practicum.ewm.service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        userService.checkUserExist(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Нельзя добавить повторный запрос");
            throw new ValidationException("Нельзя добавить повторный запрос");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("При запросе данных события возникла ошибка: Событие не найдено");
                    return new NotFoundException("Событие " + eventId + " не найдено");
                });

        if (!event.getStatus().equals(EventState.PUBLISHED)) {
            String message = String.format("Нельзя подать заявку на неопубликованное событие. " +
                    "Текущий статус события: %s", event.getStatus());
            log.warn(message);
            throw new ValidationException(message);
        }

        Integer limit = event.getParticipantLimit();
        Long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (limit > 0) { // limit = 0 means unlimited
            if (confirmedRequests >= limit) {
                String message = String.format("Лимит участников события id=%d исчерпан. " +
                        "Лимит участников: %d", eventId, limit);
                log.warn(message);
                throw new ValidationException(message);
            }
        }

        if (event.getInitiator().getId() == userId) {
            String message = "Инициатор события не может добавить запрос на участие в своём событии";
            log.warn(message);
            throw new ValidationException(message);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
                    return new NotFoundException("Пользователь " + userId + " не найден");
                });

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        request = requestRepository.save(request);

        return RequestMapper.toRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userService.checkUserExist(userId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос {} не найден", requestId);
                    return new NotFoundException("Запрос " + requestId + " не найден");
                });

        if (request.getRequester().getId() != userId) {
            log.warn("Пользователь {} не может отменить чужой запрос {}", userId, requestId);
            throw new ValidationException("Пользователь " + userId + " не может отменить чужой запрос " + requestId);
        }

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            if (event.getConfirmedRequests() != null && event.getConfirmedRequests() > 0) {
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                eventRepository.save(event);
            }
        }

        request.setStatus(RequestStatus.REJECTED);

        requestRepository.save(request);

        return RequestMapper.toRequestDto(request);
    }
}
