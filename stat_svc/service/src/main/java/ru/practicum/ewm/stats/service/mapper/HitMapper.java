package ru.practicum.ewm.stats.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.service.model.EndpointHit;

@UtilityClass
public class HitMapper {
    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(dto.getApp());
        endpointHit.setUri(dto.getUri());
        endpointHit.setIp(dto.getIp());
        endpointHit.setHitTimestamp(dto.getTimestamp());
        return endpointHit;
    }
}
