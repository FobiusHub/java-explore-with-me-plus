package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.compilation.model.Compilation;

import java.util.Optional;

/**
 * Основной репозиторий подборок.
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long>, CustomCompilationRepository {

    boolean existsByTitle(String title);

    Optional<Compilation> findByTitleIgnoreCase(String title);
}