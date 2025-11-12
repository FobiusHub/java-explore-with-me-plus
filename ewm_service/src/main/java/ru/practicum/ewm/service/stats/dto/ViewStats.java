package ru.practicum.ewm.service.stats.dto;

import lombok.Data;

/** DTO ответа GET /stats из stats-сервиса. */
@Data
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
