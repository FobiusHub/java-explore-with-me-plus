package ru.practicum.ewm.service.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.service.stats.dto.EndpointHit;
import ru.practicum.ewm.service.stats.dto.ViewStats;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private final RestClient client;
    private final String appName;
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(
            @Value("${ewm.stats.url:http://localhost:9090}") String baseUrl,
            @Value("${spring.application.name:ewm-service}") String appName
    ) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.appName = appName;
        log.info("StatsClient initialized: baseUrl={}, app={}", baseUrl, appName);
    }

    /** Регистрируем хит (уникальность считает сам stats-сервис по IP при выборке). */
    public void hit(String uri, String ip) {
        try {
            EndpointHit payload = EndpointHit.builder()
                    .app(appName)
                    .uri(uri)
                    .ip(ip)
                    .timestamp(LocalDateTime.now().format(F))
                    .build();

            client.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.debug("Stats hit sent: {} {}", ip, uri);
        } catch (Exception e) {
            log.warn("Failed to send stats hit for uri={} ip={}: {}", uri, ip, e.toString());
        }
    }

    /** Кол-во просмотров события (уникальные IP) по URI вида /events/{id}. */
    public long viewsForEvent(Long eventId) {
        String uri = "/events/" + eventId;
        return viewsForUri(uri, true);
    }

    /** Универсальный метод подсчёта просмотров по одному URI. */
    public long viewsForUri(String uri, boolean unique) {
        try {
            // широкий интервал: с 2000-01-01 до "сейчас"
            String start = "2000-01-01 00:00:00";
            String end = LocalDateTime.now().format(F);

            List<ViewStats> body = client.get()
                    .uri(builder -> builder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("unique", unique)
                            .queryParam("uris", uri) // можно повторять param для списка, тут один
                            .build())
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<ViewStats>>() {});

            if (body == null || body.isEmpty()) {
                return 0L;
            }

            // если сервис вернёт несколько записей (по разным app) — суммируем
            return body.stream()
                    .filter(v -> uri.equals(v.getUri()))
                    .mapToLong(ViewStats::getHits)
                    .sum();
        } catch (Exception e) {
            log.warn("Failed to fetch stats for uri={}: {}", uri, e.toString());
            return 0L;
        }
    }
}
