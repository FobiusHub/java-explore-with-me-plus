package ru.practicum.ewm.service.event.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.event.util.DateTimeFormatUtil;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему событию.
 * Содержит стандартизированную информацию об ошибке для возврата в API.
 *
 * <p>Автоматически устанавливает временную метку в момент создания исключения
 * и использует предопределенные значения для причины и HTTP-статуса.</p>
 *
 * @see DateTimeFormatUtil
 * @see HttpStatus
 */
@Getter
public class EventNotFoundException extends RuntimeException {

    /**
     * Стандартное описание причины ошибки.
     * Соответствует спецификации API для случаев, когда объект не найден.
     */
    private final String reason;

    /**
     * HTTP-статус, возвращаемый при возникновении этого исключения.
     * Всегда устанавливается в NOT_FOUND (404).
     */
    private final HttpStatus status;

    /**
     * Временная метка возникновения ошибки в стандартном формате.
     * Формируется автоматически при создании исключения.
     *
     * @see DateTimeFormatUtil#getLocalDateTimeStr()
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final String timeStamp;

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке, описывающее, какое событие не найдено
     */
    public EventNotFoundException(String message) {
        super(message);
        this.reason = "The required object was not found.";
        this.status = HttpStatus.NOT_FOUND;
        this.timeStamp = DateTimeFormatUtil.getLocalDateTimeStr();


    }

    @Override
    public String toString() {
        return "EventNotFoundException{" +
                "status=" + getStatus() +
                ", reason='" + getReason() +
                ", message='" + getMessage() +
                ", timeStamp='" + getTimeStamp() +
                '}';
    }
}