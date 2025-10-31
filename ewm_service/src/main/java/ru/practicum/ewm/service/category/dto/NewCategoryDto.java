package ru.practicum.ewm.service.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Название должно быть указано")
    @Size(max = 50, message = "Максимальная длина 50")
    private String name;
}
