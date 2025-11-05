package ru.practicum.ewm.service.compilation.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;

    public static ApiError of(String message, String reason, String status) {
        return ApiError.builder()
                .errors(List.of())
                .message(message)
                .reason(reason)
                .status(status)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}