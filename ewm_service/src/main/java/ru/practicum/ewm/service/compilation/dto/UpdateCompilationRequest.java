package ru.practicum.ewm.service.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UpdateCompilationRequest {

    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Длина title должна быть от 1 до 50 символов")
    private String title;
}