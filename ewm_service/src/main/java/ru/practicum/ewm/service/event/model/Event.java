package ru.practicum.ewm.service.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.category.dto.CategoryDto;

import java.time.LocalDateTime;

/**
 * Модель события.ы
 * Представляет собой основную сущность для хранения информации о мероприятиях.
 *
 * <p>Содержит полную информацию о событии, включая метаданные, настройки участия,
 * временные метки и связи с другими сущностями (категории, пользователи).</p>
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne
    @JoinColumn(name = "category_id")
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
     * Определяет, когда состоится мероприятие.
     */
    @JsonFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    /**
     * Инициатор события.
     * Пользователь, создавший мероприятие.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;

    /**
     * Местоположение события.
     * Содержит географические координаты проведения.
     */
    @Embedded
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
}