package ru.practicum.ewm.service.category.repository;

import ru.practicum.ewm.service.category.model.Category;

import java.util.List;

public interface CustomCategoryRepository {
    List<Category> findCategories(int from, int size);
}
