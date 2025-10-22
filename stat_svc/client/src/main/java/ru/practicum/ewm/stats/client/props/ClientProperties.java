package ru.practicum.ewm.stats.client.props;

import java.time.Duration;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Свойства клиента статистики.
 * Пример application.properties основного сервиса:
 *   stats.base-url=http://localhost:9090
 *   stats.connect-timeout=500ms
 *   stats.read-timeout=1s
 *   stats.hit-max-attempts=3
 *   stats.hit-backoff-millis=200
 *   stats.hit-backoff-cap-millis=2s
 */
@Component
@ConfigurationProperties(prefix = "stats")
@Validated
@Getter
@Setter
public class ClientProperties {
    @NotBlank
    private String baseUrl;

    /** Таймаут установления соединения (по умолчанию 500 мс). */
    private Duration connectTimeout = Duration.ofMillis(500);

    /** Таймаут чтения ответа (по умолчанию 1000 мс). */
    private Duration readTimeout = Duration.ofMillis(1000);

    /** Максимум попыток для hit (включая первую), по умолчанию 3. */
    @Min(1)
    private int hitMaxAttempts = 3;

    /** Базовая задержка между повторами в миллисекундах (экспоненциальная), по умолчанию 200 мс. */
    @Min(0)
    private long hitBackoffMillis = 200;

    /** Верхний предел задержки между повторами, чтобы не зависать (по умолчанию 2000 мс). */
    @Min(0)
    private long hitBackoffCapMillis = 2000;
}
