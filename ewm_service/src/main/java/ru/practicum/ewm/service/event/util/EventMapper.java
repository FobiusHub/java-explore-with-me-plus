package ru.practicum.ewm.service.event.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.mapper.CategoryMapper;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.mapper.UserMapper;

@Slf4j
@AllArgsConstructor
public class EventMapper {

    /**
     * Преобразует сущность события в краткое DTO для API.
     * Содержит только основную информацию для списков и предпросмотра.
     *
     * @return объект {@link EventShortDto} с основной информацией о событии
     */
    public static EventShortDto toEventShortDto(Event event) {

        CategoryDto category = event.getCategory() == null ? null :
                CategoryMapper.toCategoryDto(event.getCategory());

        UserShortDto initiator = event.getInitiator() == null ? null :
                UserMapper.toUserShortDto(event.getInitiator());

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(initiator)
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

        CategoryDto category = event.getCategory() == null ? null :
                CategoryMapper.toCategoryDto(event.getCategory());

        UserShortDto initiator = event.getInitiator() == null ? null :
                UserMapper.toUserShortDto(event.getInitiator());

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getStatus())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
