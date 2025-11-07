package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
