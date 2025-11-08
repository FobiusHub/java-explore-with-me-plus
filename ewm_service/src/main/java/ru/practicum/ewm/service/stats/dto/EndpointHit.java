package ru.practicum.ewm.service.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO запроса POST /hit в stats-сервис. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHit {
    private String app;        // например: "ewm-service"
    private String uri;        // например: "/events/7"
    private String ip;         // клиентский IP
    private String timestamp;  // "yyyy-MM-dd HH:mm:ss"
}
