package ru.practicum.ewm.service.event.utill;

import ru.practicum.ewm.service.category.mapper.CategoryMapper;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.mapper.UserMapper;

public class EventMapper {

    /**
     * Преобразует сущность события в краткое DTO для API.
     * Содержит только основную информацию для списков и предпросмотра.
     *
     * @return объект {@link EventShortDto} с основной информацией о событии
     */
    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    /**
     * Преобразует сущность события в полное DTO для API.
     * Содержит полную информацию для списков и просмотра.
     *
     * @return объект {@link EventFullDto} с основной информацией о событии
     */
    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
