package ru.practicum.ewm.service.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private long id;

    private LocalDateTime created;

    private long event;

    private long requester;

    private String status;
}
