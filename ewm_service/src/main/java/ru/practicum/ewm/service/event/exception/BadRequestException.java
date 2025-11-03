package ru.practicum.ewm.service.event.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

/**
 * Исключение, выбрасываемое при некорректном запросе к API.
 * Содержит стандартизированную информацию об ошибке для возврата клиенту.
 *
 * <p>Используется для обработки ошибок валидации и бизнес-логики,
 * связанных с неправильно сформированными или некорректными запросами.</p>
 *
 * @see DateTimeFormatUtil
 * @see HttpStatus
 */
@Getter
public class BadRequestException extends RuntimeException {

    /**
     * Стандартное описание причины ошибки.
     * Соответствует спецификации API для случаев некорректного запроса.
     */
    private final String reason = "Incorrectly made request.";

    /**
     * HTTP-статус, возвращаемый при возникновении этого исключения.
     * Всегда устанавливается в BAD_REQUEST (400).
     */
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    /**
     * Временная метка возникновения ошибки в стандартном формате.
     * Формируется автоматически при создании исключения.
     *
     * @see DateTimeFormatUtil#getLocalDateTimeStr()
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final String timeStamp = DateTimeFormatUtil.getLocalDateTimeStr();

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке, описывающее, что именно некорректно в запросе
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Возвращает строковое представление исключения со всей информацией об ошибке.
     * Используется для логирования и отладки.
     *
     * @return строковое представление исключения с полями status, reason, message и timeStamp
     */
    @Override
    public String toString() {
        return "BadRequestException{" +
                "status=" + getStatus() +
                ", reason='" + getReason() +
                ", message='" + getMessage() +
                ", timeStamp='" + getTimeStamp() +
                '}';
    }
}