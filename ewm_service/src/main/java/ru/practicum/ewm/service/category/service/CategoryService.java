package ru.practicum.ewm.service.category.service;

import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(long categoryId);

    CategoryDto update(long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(long categoryId);
}
