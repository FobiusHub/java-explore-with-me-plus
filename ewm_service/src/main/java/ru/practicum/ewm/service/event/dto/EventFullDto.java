package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для передачи полной информации о событии.
 * Содержит все данные о событии, включая метаданные, настройки и статистику.
 *
 * <p>Используется для возврата детальной информации о событии в API ответах,
 * как в публичном, так и в административном API.</p>
 *
 * @see CategoryDto
 * @see UserShortDto
 * @see Location
 * @see EventState
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    /**
     * Уникальный идентификатор события
     */
    private Long id;

    /**
     * Краткое описание события
     */
    private String annotation;

    /**
     * Категория события
     */
    private CategoryDto category;

    /**
     * Количество подтвержденных запросов на участие
     */
    private Long confirmedRequests;

    /**
     * Дата и время создания события
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    /**
     * Полное описание события
     */
    private String description;

    /**
     * Дата и время проведения события
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    /**
     * Инициатор события (пользователь, создавший событие)
     */
    private UserShortDto initiator;

    /**
     * Местоположение события с географическими координатами
     */
    private Location location;

    /**
     * Флаг, указывающий является ли событие платным
     */
    private Boolean paid;

    /**
     * Ограничение на количество участников (0 - без ограничений)
     */
    @JsonProperty(defaultValue = "0")
    private Integer participantLimit;

    /**
     * Дата и время публикации события
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    /**
     * Флаг, указывающий требуется ли модерация заявок на участие
     */
    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    /**
     * Текущее состояние события в системе
     */
    private EventState state;

    /**
     * Заголовок события
     */
    private String title;

    /**
     * Количество просмотров события
     */
    private Long views;
}