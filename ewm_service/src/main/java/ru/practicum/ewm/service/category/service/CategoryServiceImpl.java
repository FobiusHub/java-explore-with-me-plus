package ru.practicum.ewm.service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;
import ru.practicum.ewm.service.category.mapper.CategoryMapper;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.common.exception.ValidationException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        validateNameUnique(name);
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //ДОРАБОТАТЬ С УЧЕТОМ EVENT - 409 ЕСЛИ ЕСТЬ СВЯЗАННЫЕ СОБЫТИЯ
    @Override
    public void delete(long categoryId) {
        checkCategoryExist(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    //ДОРАБОТАТЬ С УЧЕТОМ EVENT - 409 ЕСЛИ ЕСТЬ СВЯЗАННЫЕ СОБЫТИЯ
    @Override
    public CategoryDto update(long categoryId, NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        validateNameUnique(name);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("При запросе категории возникла ошибка: категория не найдена");
                    return new NotFoundException("Категория " + categoryId + " не найдена");
                });
        category.setName(name);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        return categoryRepository.findCategories(from, size).stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto get(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("При запросе категории возникла ошибка: категория не найдена");
                    return new NotFoundException("Категория " + categoryId + " не найдена");
                });
        return CategoryMapper.toCategoryDto(category);
    }

    private void checkCategoryExist(long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            log.warn("При запросе категории возникла ошибка: категория не найдена");
            throw new NotFoundException("Категория " + categoryId + " не найдена");
        }
    }

    private void validateNameUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            log.warn("При проверке названия возникла ошибка: название уже существует");
            throw new ValidationException("Название " + name + " уже существует");
        }
    }
}
