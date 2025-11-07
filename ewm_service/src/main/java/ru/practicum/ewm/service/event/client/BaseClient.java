package ru.practicum.ewm.service.event.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.client.exceptions.StatsClientException;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BaseClient {

    private static final String POST_STATS_PREFIX = "/hit";
    private static final String GET_STATS_PREFIX = "/stats";
    private final RestTemplate restTemplate;
    private final String serverUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BaseClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl,
                      RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public <T> ResponseEntity<Object> post(T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<Object> explorewithmeServerResponse;
        try {
            explorewithmeServerResponse = restTemplate.exchange(POST_STATS_PREFIX, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(explorewithmeServerResponse);
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        // Форматируем даты в строки
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        // Строим URL с параметрами
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + GET_STATS_PREFIX)
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .queryParam("unique", unique);

        // Добавляем URIs, если они переданы
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        String url = builder.build().toUriString();
        HttpEntity<?> requestEntity = new HttpEntity<>(null);

        ResponseEntity<Object> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            throw new StatsClientException("Failed to get stats from stats-server: " + e.getMessage(), e);
        }

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            // Конвертируем ответ в List<ViewStatsDto>
            return objectMapper.convertValue(response.getBody(),
                    new TypeReference<List<ViewStatsDto>>() {
                    });
        }
        throw new StatsClientException("Failed to get stats from stats-server. Status: " + response.getStatusCode(), null);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}


//    private static final String API_PREFIX = "/hit";
//    private final RestTemplate restTemplate;
//
//    public BaseClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl,
//                      RestTemplateBuilder builder) {
//        this.restTemplate = builder
//                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
//                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
//                .build();
//    }
//
//    public <T> ResponseEntity<Object> sendRequest(T body) {
//        HttpEntity<T> requestEntity = new HttpEntity<>(body);
//
//        ResponseEntity<Object> explorewithmeServerResponse;
//        try {
//            explorewithmeServerResponse = restTemplate.exchange(API_PREFIX, HttpMethod.POST, requestEntity, Object.class);
//        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
//        }
//        return prepareGatewayResponse(explorewithmeServerResponse);
//    }
//
//    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return response;
//        }
//
//        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
//
//        if (response.hasBody()) {
//            return responseBuilder.body(response.getBody());
//        }
//        return responseBuilder.build();
//    }




