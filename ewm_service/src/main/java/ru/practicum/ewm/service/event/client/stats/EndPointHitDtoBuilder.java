package ru.practicum.ewm.service.event.client.stats;

import lombok.Builder;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;

@Builder
@Component
public class EndPointHitDtoBuilder {
    private final String app = "ewm-main-service";

    public EndpointHitDto getEndpointHitDto(String uri, String ip) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(app);
        endpointHitDto.setUri(uri);
        endpointHitDto.setIp(ip);
        endpointHitDto.setTimestamp(LocalDateTime.now());

        return endpointHitDto;
    }
}
