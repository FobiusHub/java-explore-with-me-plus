package ru.practicum.ewm.service.event.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
