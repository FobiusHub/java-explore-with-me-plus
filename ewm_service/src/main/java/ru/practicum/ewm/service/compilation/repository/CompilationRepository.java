package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.compilation.model.Compilation;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Основной репозиторий подборок.
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    boolean existsByTitle(String title);
    List<Compilation> findByPinned(boolean pinned, Pageable pageable);
}