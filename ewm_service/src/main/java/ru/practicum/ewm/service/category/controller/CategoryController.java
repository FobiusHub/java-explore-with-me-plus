package ru.practicum.ewm.service.category.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    /*
    В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
    */
    @GetMapping
    public List<CategoryDto> get(@RequestParam(defaultValue = "0") @Min(0) int from,
                                 @RequestParam(defaultValue = "10") @Min(1) int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("{id}")
    public CategoryDto get(@PathVariable long id) {
        return categoryService.get(id);
    }
}
