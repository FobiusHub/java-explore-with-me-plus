package ru.practicum.ewm.service.event.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;

/**
 * Глобальный обработчик исключений для событий.
 * Перехватывает исключения, возникающие в контроллерах, и преобразует их в стандартизированные HTTP-ответы.
 *
 * @see EventNotFoundException
 * @see BadRequestException
 * @see ApiError
 */
@Slf4j
@RestControllerAdvice
public class EventExceptionHandler {

    /**
     * Обрабатывает исключения, связанные с ненайденными событиями.
     * Возвращает HTTP-статус 404 (Not Found) с деталями ошибки.
     *
     * @param exception перехваченное исключение EventNotFoundException
     * @return стандартизированный объект ошибки {@link ApiError}
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError eventNotFoundException(final EventNotFoundException exception) {
        log.warn(exception.toString());
        return ApiError.builder()
                .message(exception.getMessage())
                .reason(exception.getReason())
                .status(exception.getStatus())
                .timeStamp(exception.getTimeStamp())
                .build();
    }

    /**
     * Обрабатывает исключения, связанные с некорректными запросами.
     * Возвращает HTTP-статус 400 (Bad Request) с деталями ошибки.
     *
     * @param exception перехваченное исключение BadRequestException
     * @return стандартизированный объект ошибки {@link ApiError}
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestException(final BadRequestException exception) {
        log.warn(exception.toString());
        return ApiError.builder()
                .message(exception.getMessage())
                .reason(exception.getReason())
                .status(exception.getStatus())
                .timeStamp(exception.getTimeStamp())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestException(final ConstraintViolationException exception) {
        log.warn(exception.toString());
        return ApiError.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timeStamp(DateTimeFormatUtil.getLocalDateTimeStr())
                .build();
    }
}