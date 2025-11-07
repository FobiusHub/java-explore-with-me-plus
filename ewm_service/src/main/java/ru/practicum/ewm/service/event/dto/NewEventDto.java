package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    /**
     * default participant limit = 0 means "unlimited"
     */
    @PositiveOrZero
    @Builder.Default
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    /**
     * Minimum allowed time interval between event creation and event start time.
     * Event cannot be scheduled sooner than this many hours from the current moment.
     */
    private static final long MIN_HOURS_TO_EVENT = 2;

    @AssertTrue(message = "Event must be scheduled at least " + MIN_HOURS_TO_EVENT + " hours in advance")
    private boolean isEventDateValid() {
        if (eventDate != null) {
            return eventDate.isAfter(LocalDateTime.now().plusHours(MIN_HOURS_TO_EVENT));
        }
        return true;
    }
}