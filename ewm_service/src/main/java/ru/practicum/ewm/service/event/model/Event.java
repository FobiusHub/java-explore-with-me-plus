package ru.practicum.ewm.service.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import ru.practicum.ewm.service.common.commonDto.Category;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.common.commonDto.CategoryDto;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.mapper.UserMapper;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;

/**
 * Модель события в системе.
 * Представляет собой основную сущность для хранения информации о мероприятиях.
 *
 * <p>Содержит полную информацию о событии, включая метаданные, настройки участия,
 * временные метки и связи с другими сущностями (категории, пользователи).</p>
 *
 * <p>Предоставляет методы преобразования в DTO для API.</p>
 *
 * @see EventState
 * @see CategoryDto
 * @see UserShortDto
 * @see Location
 * @see EventFullDto
 * @see EventShortDto
 */

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    /**
     * Уникальный идентификатор события.
     */
    @Id
    private Long id;

    /**
     * Краткое описание события.
     * Используется для поиска и предварительного просмотра.
     */
    private String annotation;

    /**
     * Категория события.
     * Определяет тематическую принадлежность мероприятия.
     */
    private Category category;

    /**
     * Количество подтвержденных запросов на участие.
     * Обновляется при подтверждении/отклонении заявок.
     */
    private Long confirmedRequests;

    /**
     * Дата и время создания события.
     * Форматируется в стандартном формате "yyyy-MM-dd HH:mm:ss".
     */
    @JsonFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    /**
     * Полное описание события.
     * Содержит детальную информацию о мероприятии.
     */
    private String description;

    /**
     * Дата и время проведения события в формате гггг-ММ-дд ЧЧ:мм:сс.
     * Определяет когда состоится мероприятие.
     */
    @JsonFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    /**
     * Инициатор события.
     * Пользователь, создавший мероприятие.
     */
    @OneToMany()
    private User initiator;

    /**
     * Местоположение события.
     * Содержит географические координаты проведения.
     */
    private Location location;

    /**
     * Флаг платности события.
     * true - участие платное, false - бесплатное.
     */
    private Boolean paid;

    /**
     * Лимит участников.
     * 0 - без ограничений, >0 - максимальное количество участников.
     * Значение по умолчанию: 0.
     */
    @JsonProperty(defaultValue = "0")
    private Integer participantLimit;

    /**
     * Дата и время публикации события.
     * Заполняется при переходе события в состояние PUBLISHED.
     */
    @JsonFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    /**
     * Флаг модерации заявок.
     * true - заявки проходят модерацию, false - автоматическое подтверждение.
     * Значение по умолчанию: true.
     */
    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    /**
     * Текущее состояние события в системе.
     * Определяет видимость и доступность мероприятия.
     */
    private EventState state;

    /**
     * Заголовок события.
     * Основное название мероприятия.
     */
    private String title;

    /**
     * Количество просмотров события.
     * Увеличивается при каждом просмотре детальной страницы.
     */
    private Long views;

    /**
     * Преобразует сущность события в полное DTO для API.
     *
     * @return объект {@link EventFullDto} со всей информацией о событии
     */
    public EventFullDto toEventFullDto() {
        return EventFullDto.builder()
                .id(id)
                .annotation(annotation)
                .category(category.toCategoryDto())
                .confirmedRequests(confirmedRequests)
                .createdOn(createdOn)
                .description(description)
                .eventDate(eventDate)
                .initiator(UserMapper.toUserShortDto(initiator))
                .location(location)
                .paid(paid)
                .participantLimit(participantLimit)
                .publishedOn(publishedOn)
                .requestModeration(requestModeration)
                .state(state)
                .title(title)
                .views(views)
                .build();
    }

    /**
     * Преобразует сущность события в краткое DTO для API.
     * Содержит только основную информацию для списков и предпросмотра.
     *
     * @return объект {@link EventShortDto} с основной информацией о событии
     */
    public EventShortDto toEventShortDto() {
        return EventShortDto.builder()
                .annotation(annotation)
                .category(category.toCategoryDto())
                .confirmedRequests(confirmedRequests)
                .eventDate(eventDate)
                .initiator(UserMapper.toUserShortDto(initiator))
                .paid(paid)
                .title(title)
                .views(views)
                .build();
    }
}