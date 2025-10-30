package ru.practicum.ewm.service.event.utill;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитарный класс для работы с форматами даты и времени.
 * Предоставляет константы и методы для стандартизированного форматирования временных меток.
 *
 * <p>Класс содержит предопределенные форматы, используемые во всем приложении
 * для обеспечения единообразия в отображении дат и времени.</p>
 *
 * <p>Этот класс не предназначен для наследования и инстанцирования.</p>
 */
@Getter
public class DateTimeFormatUtil {

    /**
     * Строковый шаблон формата даты и времени.
     * Использует формат: ГГГГ-ММ-ДД ЧЧ:мм:сс
     *
     * <p>Пример: "2024-01-15 14:30:25"</p>
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Форматтер даты и времени, созданный на основе {@link #DATE_TIME_FORMAT}.
     * Может использоваться для парсинга и форматирования объектов {@link LocalDateTime}.
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    /**
     * Возвращает текущую дату и время в виде строки в стандартном формате.
     *
     * @return текущая дата и время в формате "ГГГГ-ММ-ДД ЧЧ:мм:сс"
     *
     * @example "2024-01-15 14:30:25"
     */
    public static String getLocalDateTimeStr() {
        return LocalDateTime.now().format(DateTimeFormatUtil.DATE_TIME_FORMATTER);
    }
}