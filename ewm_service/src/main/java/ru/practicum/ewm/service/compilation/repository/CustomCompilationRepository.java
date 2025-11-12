package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.compilation.model.Compilation;

import java.util.List;

/**
 * Кастомные методы для работы с подборками.
 */
public interface CustomCompilationRepository {

    /**
     * Получение подборок с фильтром по pinned и пагинацией.
     */
    List<Compilation> findCompilations(Boolean pinned, Pageable pageable);
}
