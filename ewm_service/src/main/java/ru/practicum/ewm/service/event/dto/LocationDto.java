package ru.practicum.ewm.service.event.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    private double lat;
    private double lon;
}
