package ru.practicum.ewm.service.compilation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
public class NewCompilationDto {

    @Builder.Default
    private Set<Long> events = new HashSet<>();

    @Builder.Default
    private Boolean pinned = false;

    @NotNull(message = "Поле title не должно быть пустым")
    @Size(min = 1, max = 50, message = "Длина title должна быть от 1 до 50 символов")
    private String title;
}