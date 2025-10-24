package ru.practicum.ewm.stats.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.mapper.HitMapper;
import ru.practicum.ewm.stats.service.model.EndpointHit;
import ru.practicum.ewm.stats.service.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HitRepository hitRepository;

    @Transactional
    @Override
    public void create(EndpointHitDto hitDto) {
        EndpointHit endpointHit = HitMapper.toEndpointHit(hitDto);
        hitRepository.save(endpointHit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> viewStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime ldtStart = LocalDateTime.parse(start, FMT);
        LocalDateTime ldtEnd = LocalDateTime.parse(end, FMT);

        if (ldtStart.equals(ldtEnd)) {
            log.warn("Даты начала и окончания должны различаться");
            throw new IllegalArgumentException("Даты начала и окончания должны различаться");
        }

        if (ldtStart.isAfter(ldtEnd)) {
            log.warn("Дата начала не может быть после даты окончания");
            throw new IllegalArgumentException("Дата начала не может быть после даты окончания");
        }

        List<ViewStatsDto> result;

        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                result = hitRepository.getUniqueStatsByUris(ldtStart, ldtEnd, uris);
            } else {
                result = hitRepository.getStatsByUris(ldtStart, ldtEnd, uris);
            }
        } else {
            if (unique) {
                result = hitRepository.getUniqueStats(ldtStart, ldtEnd);
            } else {
                result = hitRepository.getStats(ldtStart, ldtEnd);
            }
        }

        return result;
    }
}
