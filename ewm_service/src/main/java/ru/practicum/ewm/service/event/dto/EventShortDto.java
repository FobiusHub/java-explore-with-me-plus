package ru.practicum.ewm.service.event.dto;

import lombok.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}