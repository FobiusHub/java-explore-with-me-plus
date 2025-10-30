package ru.practicum.ewm.service.event.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;

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
    private final String reason = "The required object was not found.";

    /**
     * HTTP-статус, возвращаемый при возникновении этого исключения.
     * Всегда устанавливается в NOT_FOUND (404).
     */
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    /**
     * Временная метка возникновения ошибки в стандартном формате.
     * Формируется автоматически при создании исключения.
     *
     * @see DateTimeFormatUtil#getLocalDateTimeStr()
     */
    private final String timeStamp = DateTimeFormatUtil.getLocalDateTimeStr();

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке, описывающее, какое событие не найдено
     */
    public EventNotFoundException(String message) {
        super(message);
    }
}