package ru.practicum.ewm.service.compilation.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ru.practicum.ewm.service.compilation.model.Compilation;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CompilationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompilationRepository compilationRepository;

    private Compilation pinnedCompilation;
    private Compilation unpinnedCompilation;

    @BeforeEach
    void setUp() {
        pinnedCompilation = new Compilation();
        pinnedCompilation.setTitle("Закрепленная подборка");
        pinnedCompilation.setPinned(true);
        pinnedCompilation.setEvents(Set.of(1L, 2L));

        unpinnedCompilation = new Compilation();
        unpinnedCompilation.setTitle("Незакрепленная подборка");
        unpinnedCompilation.setPinned(false);
        unpinnedCompilation.setEvents(Set.of(3L, 4L));
    }

    @Test
    void existsByTitle_shouldReturnTrue_whenTitleExists() {
        // Сохраняем подборку
        entityManager.persist(pinnedCompilation);
        entityManager.flush();

        // Проверяем существование по названию
        boolean exists = compilationRepository.existsByTitle(pinnedCompilation.getTitle());

        assertTrue(exists);
    }

    @Test
    void existsByTitle_shouldReturnFalse_whenTitleNotExists() {
        // Проверяем существование по названию
        boolean exists = compilationRepository.existsByTitle("Несуществующая подборка");

        assertFalse(exists);
    }

    @Test
    void findCompilations_shouldReturnAllCompilations_whenPinnedIsNull() {
        // Подготавливаем данные
        entityManager.persist(pinnedCompilation);
        entityManager.persist(unpinnedCompilation);
        entityManager.flush();

        // Задаем параметры пагинации
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        // Выполняем запрос
        List<Compilation> compilations = compilationRepository.findCompilations(null, pageable);

        // Проверяем результаты
        assertNotNull(compilations);
        assertEquals(2, compilations.size());
        assertEquals(pinnedCompilation.getTitle(), compilations.get(0).getTitle());
        assertEquals(unpinnedCompilation.getTitle(), compilations.get(1).getTitle());
    }

    @Test
    void findCompilations_shouldReturnOnlyPinnedCompilations_whenPinnedIsTrue() {
        // Подготавливаем данные
        entityManager.persist(pinnedCompilation);
        entityManager.persist(unpinnedCompilation);
        entityManager.flush();

        // Задаем параметры пагинации
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        // Выполняем запрос
        List<Compilation> compilations = compilationRepository.findCompilations(true, pageable);

        // Проверяем результаты
        assertNotNull(compilations);
        assertEquals(1, compilations.size());
        assertEquals(pinnedCompilation.getTitle(), compilations.getFirst().getTitle());
        assertTrue(compilations.getFirst().isPinned());
    }

    @Test
    void findCompilations_shouldReturnOnlyUnpinnedCompilations_whenPinnedIsFalse() {
        // Подготавливаем данные
        entityManager.persist(pinnedCompilation);
        entityManager.persist(unpinnedCompilation);
        entityManager.flush();

        // Задаем параметры пагинации
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        // Выполняем запрос
        List<Compilation> compilations = compilationRepository.findCompilations(false, pageable);

        // Проверяем результаты
        assertNotNull(compilations);
        assertEquals(1, compilations.size());
        assertEquals(unpinnedCompilation.getTitle(), compilations.getFirst().getTitle());
        assertFalse(compilations.getFirst().isPinned());
    }
}