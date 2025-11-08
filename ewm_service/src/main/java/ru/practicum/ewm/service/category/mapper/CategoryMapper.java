package ru.practicum.ewm.service.category.mapper;

import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.model.Category;

public final class CategoryMapper {
    private CategoryMapper(){}

    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
