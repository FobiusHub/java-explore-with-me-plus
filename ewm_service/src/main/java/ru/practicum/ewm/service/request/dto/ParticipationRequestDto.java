package ru.practicum.ewm.service.request.dto;

import lombok.Data;
import ru.practicum.ewm.service.request.model.RequestStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;

    private LocalDateTime created;

    private long event;

    private long requester;

    private RequestStatus status;
}
