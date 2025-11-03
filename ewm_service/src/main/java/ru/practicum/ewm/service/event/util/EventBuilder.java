package ru.practicum.ewm.service.event.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class EventBuilder {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Event buildEventBy(NewEventDto newEventDto) {
        Long initiatorId = newEventDto.getInitiatorId();
        User initiator = getUser(initiatorId);

        Long categoryId = newEventDto.getCategoryId();
        Category category = getCategory(categoryId);

        long DEFAULT_CONFIRMED_REQUESTS = 0L;
        long DEFAULT_VIEWS = 0L;
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(DEFAULT_CONFIRMED_REQUESTS)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .views(DEFAULT_VIEWS)
                .build();

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("При запросе данных пользователя возникла ошибка: Пользователь id={} не найден", userId);
                    return new NotFoundException("Пользователь id=" + userId + " не найден");
                });
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("При запросе данных пользователя возникла ошибка: Категория id={} не найдена", categoryId);
                    return new NotFoundException("Категория id=" + categoryId + " не найдена");
                });
    }
}
