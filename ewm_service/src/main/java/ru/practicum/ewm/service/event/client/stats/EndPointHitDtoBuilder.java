package ru.practicum.ewm.service.event.client.stats;

import lombok.Builder;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

@Builder
 class EndPointHitDtoBuilder {
    private final String app = "ewm-main-service";

    public EndpointHitDto getEndpointHitDto(String uri, String ip){
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(app);
        endpointHitDto.setUri(uri);
        endpointHitDto.setIp(ip);
        endpointHitDto.setTimestamp(DateTimeFormatUtil.getLocalDateTime());

        return endpointHitDto;
    }
}
