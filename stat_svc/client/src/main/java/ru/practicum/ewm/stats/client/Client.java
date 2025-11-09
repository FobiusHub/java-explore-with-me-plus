package ru.practicum.ewm.stats.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.client.props.ClientProperties;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class Client {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;
    private final ClientProperties props;

    public Client(@NonNull RestTemplateBuilder builder,
                       @NonNull ClientProperties props) {
        this.props = props;
        this.rest = builder
                .rootUri(props.getBaseUrl())
                .setConnectTimeout(props.getConnectTimeout())
                .setReadTimeout(props.getReadTimeout())
                .build();
        log.debug("StatsClient initialized: baseUrl={}, connectTimeout={}, readTimeout={}, hitMaxAttempts={}, hitBackoff={}ms",
                props.getBaseUrl(), props.getConnectTimeout(), props.getReadTimeout(),
                props.getHitMaxAttempts(), props.getHitBackoffMillis());
    }

    /**
     * POST /hit
     * Мягкая деградация: ошибки логируем и подавляем (основной сервис не падает).
     * Ретраи с экспоненциальной задержкой: base * 2^(attempt-1), с верхним пределом.
     */
    public void hit(@NonNull EndpointHitDto dto) {
        int attempt = 1;
        final int max = props.getHitMaxAttempts();
        final long baseBackoff = props.getHitBackoffMillis();
        final long cap = props.getHitBackoffCapMillis();

        while (true) {
            try {
                // сервер отвечает 201 + JSON; можем получить тело, хотя оно нам не критично
                rest.postForEntity("/hit", dto, EndpointHitDto.class);
                if (log.isTraceEnabled()) {
                    log.trace("POST /hit sent: app={}, uri={}, ip={}, ts={}",
                            dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
                }
                return;
            } catch (RestClientException ex) {
                if (attempt >= max) {
                    log.warn("StatsClient: POST /hit failed after {} attempt(s). Continue without stats. reason={}",
                            attempt, ex.toString(), ex);
                    return;
                }
                long delay = baseBackoff * (1L << (attempt - 1));
                if (delay > cap) delay = cap;
                log.warn("StatsClient: POST /hit failed on attempt {}/{}. Retry in {} ms. reason={}",
                        attempt, max, delay, ex.toString());
                safeSleep(delay);
                attempt++;
            } catch (RuntimeException ex) {
                log.warn("StatsClient: unexpected error on POST /hit. Continue without stats. {}", ex.toString(), ex);
                return;
            }
        }
    }

    /**
     * GET /stats
     * Исключения НЕ подавляем по твоему требованию.
     */

    public List<ViewStatsDto> stats(@NonNull LocalDateTime start,
                                    @NonNull LocalDateTime end,
                                    List<String> uris,
                                    boolean unique) {

        UriComponentsBuilder b = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", FMT.format(start))
                .queryParam("end",   FMT.format(end))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            b.queryParam("uris", uris.toArray(String[]::new)); // множественные ?uris=
        }

        // ВАЖНО: кодируем пробелы и прочие спецсимволы
        URI uri = b.build().encode().toUri();

        ResponseEntity<ViewStatsDto[]> resp = rest.getForEntity(uri, ViewStatsDto[].class);
        ViewStatsDto[] body = resp.getBody();
        return (body == null) ? Collections.emptyList() : Arrays.asList(body);
    }

    private void safeSleep(long millis) {
        try {
            if (millis > 0) Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("StatsClient: retry sleep interrupted; giving up retries.");
        }
    }
}
