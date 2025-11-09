package ru.practicum.ewm.stats.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.service.model.EndpointHit;

@UtilityClass
public class HitMapper {

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        EndpointHit e = new EndpointHit();
        e.setApp(dto.getApp());
        e.setUri(dto.getUri());
        e.setIp(dto.getIp());
        e.setHitTimestamp(dto.getTimestamp());
        return e;
    }

    public EndpointHitDto toDto(EndpointHit e) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setId(e.getId());
        dto.setApp(e.getApp());
        dto.setUri(e.getUri());
        dto.setIp(e.getIp());
        dto.setTimestamp(e.getHitTimestamp());
        return dto;
    }
}