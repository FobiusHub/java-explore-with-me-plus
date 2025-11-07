package ru.practicum.ewm.service.event.client.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.event.client.BaseClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class StatsServiceDriver {

    private final BaseClient client;
    private final EndPointHitDtoBuilder endPointHitDtoBuilder;

    public void create(HttpServletRequest httpServletRequest) {
        try {
            String ip = httpServletRequest.getRemoteAddr();
            String uri = httpServletRequest.getRequestURI();

            log.debug("Sending stats for URI: {}, IP: {}", uri, ip);

            EndpointHitDto endpointHitDto = endPointHitDtoBuilder.getEndpointHitDto(uri, ip);
            ResponseEntity<Object> response = client.post(endpointHitDto);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Successfully sent stats to service");
            } else {
                log.warn("Stats service returned non-success status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception exception) {
            log.error("Failed to send request to stats service: {}", exception.getMessage());
        }
    }

    public List<ViewStatsDto> get (LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
           return client.get(start, end, uris, unique);
        } catch (Exception exception) {
            log.error("Failed to get response from stats service: {}", exception.getMessage());
        }
        return Collections.emptyList();
    }


}