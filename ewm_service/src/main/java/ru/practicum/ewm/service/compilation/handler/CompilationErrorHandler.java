package ru.practicum.ewm.service.compilation.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.service.compilation.exception.ApiError;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.exception.TitleAlreadyExistsException;

import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "ru.practicum.ewm.service.compilation")
public class CompilationErrorHandler {

    @ExceptionHandler(CompilationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(CompilationNotFoundException e) {
        return ApiError.of(e.getMessage(), "The required object was not found.", "NOT_FOUND");
    }

    @ExceptionHandler(TitleAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(TitleAlreadyExistsException e) {
        return ApiError.of(e.getMessage(), "Integrity constraint has been violated.", "CONFLICT");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> String.format("Field: %s. Error: %s. Value: %s",
                        err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
                .collect(Collectors.joining("; "));

        return ApiError.of(message, "Incorrectly made request.", "BAD_REQUEST");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraint(ConstraintViolationException e) {
        return ApiError.of(e.getMessage(), "Integrity constraint has been violated.", "CONFLICT");
    }
}