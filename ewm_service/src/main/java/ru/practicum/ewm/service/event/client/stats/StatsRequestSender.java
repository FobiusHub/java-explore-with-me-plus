package ru.practicum.ewm.service.event.client.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.event.client.BaseClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

@Slf4j
@Component
@AllArgsConstructor
public class StatsRequestSender {

    private final BaseClient client;
    private final EndPointHitDtoBuilder endPointHitDtoBuilder;

    public void sendRequestToStatService(HttpServletRequest httpServletRequest) {
        try {
            String ip = httpServletRequest.getRemoteAddr();
            String uri = httpServletRequest.getRequestURI();

            log.debug("Sending stats for URI: {}, IP: {}", uri, ip);

            EndpointHitDto endpointHitDto = endPointHitDtoBuilder.getEndpointHitDto(uri, ip);
            ResponseEntity<Object> response = client.sendRequest(endpointHitDto);

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
}