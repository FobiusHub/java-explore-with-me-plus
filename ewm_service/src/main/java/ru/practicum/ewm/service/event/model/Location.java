package ru.practicum.ewm.service.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Float lat;
    private Float lon;
}
