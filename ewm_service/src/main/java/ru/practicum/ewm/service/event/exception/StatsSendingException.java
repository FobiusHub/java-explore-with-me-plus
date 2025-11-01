//package ru.practicum.ewm.service.event.exception;
//
//import lombok.Getter;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;
//
//@Getter
//public class StatsSendingException extends RuntimeException {
//
//    /**
//     * Описание причины ошибки.
//     * Зависит от типа HTTP статуса, полученного от сервиса статистики.
//     */
//    private final String reason;
//
//    /**
//     * HTTP-статус, возвращаемый сервисом статистики.
//     */
//    private final HttpStatus status;
//
//    /**
//     * Временная метка возникновения ошибки.
//     */
//    private final String timeStamp;
//
//    /**
//     * Конструктор для создания исключения на основе HTTP статуса от сервиса статистики.
//     *
//     * @param httpStatusCode HTTP статус, полученный от сервиса статистики
//     */
//    public StatsSendingException(HttpStatusCode httpStatusCode) {
//        super("Failed to communicate with statistics service. Status: " + httpStatusCode);
//        this.status = convertToHttpStatus(httpStatusCode);
//        this.reason = determineReason(this.status);
//        this.timeStamp = DateTimeFormatUtil.getLocalDateTimeStr();
//    }
//
//    /**
//     * Определение причины ошибки по HTTP статусу.
//     */
//    private String determineReason(HttpStatus status) {
//        if (status.is4xxClientError()) {
//            return "Invalid request to statistics service";
//        } else if (status.is5xxServerError()) {
//            return "Statistics service temporarily unavailable";
//        } else {
//            return "Unexpected response from statistics service";
//        }
//    }
//
//    /**
//     * Преобразование HttpStatusCode в HttpStatus.
//     */
//    private HttpStatus convertToHttpStatus(HttpStatusCode httpStatusCode) {
//        try {
//            return HttpStatus.valueOf(httpStatusCode.value());
//        } catch (IllegalArgumentException e) {
//            // Если статус неизвестен, возвращаем INTERNAL_SERVER_ERROR
//            return HttpStatus.INTERNAL_SERVER_ERROR;
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "EventNotFoundException{" +
//                "status=" + getStatus() +
//                ", reason='" + getReason() +
//                ", message='" + getMessage() +
//                ", timeStamp='" + getTimeStamp() +
//                '}';
//    }
//}