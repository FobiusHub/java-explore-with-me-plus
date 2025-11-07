package ru.practicum.ewm.service.event.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.ewm.service.event.enums.StateAction;
import ru.practicum.ewm.service.event.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDto {
    @Positive
    private Long id;
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
