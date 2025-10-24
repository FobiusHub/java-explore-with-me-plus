package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.service.model.EndpointHit;
import ru.practicum.ewm.stats.service.repository.HitRepository;
import ru.practicum.ewm.stats.service.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HitServiceImplTest {
    private final HitService hitService;
    private final HitRepository hitRepository;

    @Test
    void createShouldCorrectlySaveDto() {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("ewm-main-service");
        hitDto.setUri("/someUri/1");
        hitDto.setIp("10.10.10.13");
        hitDto.setTimestamp(LocalDateTime.of(2025, 10, 1, 13, 50));

        hitService.create(hitDto);
        List<EndpointHit> hitList = hitRepository.findAll();
        EndpointHit hit = hitList.getFirst();

        assertThat(hitList, hasSize(1));
        assertThat(hit, notNullValue());
        assertThat(hit.getApp(), equalTo(hitDto.getApp()));
        assertThat(hit.getUri(), equalTo(hitDto.getUri()));
        assertThat(hit.getHitTimestamp(), equalTo(hitDto.getTimestamp()));
    }

    @Test
    void viewStatsShouldThrowIllegalArgumentExceptionIfStartAndEndIsEqual() {
        String date = "2025-01-01 12:40:00";
        assertThrows(IllegalArgumentException.class, () -> {
            hitService.viewStats(date, date, null, true);
        });
    }

    @Test
    void viewStatsShouldThrowIllegalArgumentExceptionIfStartIsAfterThanEnd() {
        String start = "2026-01-01 12:40:00";
        String end = "2025-01-01 12:40:00";
        assertThrows(IllegalArgumentException.class, () -> {
            hitService.viewStats(start, end, null, true);
        });
    }
}
