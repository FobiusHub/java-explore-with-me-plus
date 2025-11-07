package ru.practicum.ewm.service.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(length = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;

    @Embedded
    private Location location;

    private Boolean paid;

    @JsonProperty(defaultValue = "0")
    private Integer participantLimit;

    private Integer confirmedParticipantRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EventState state;

    @Column(length = 120)
    private String title;

    private Long views;
}