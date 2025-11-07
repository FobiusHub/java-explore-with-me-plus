package ru.practicum.ewm.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.filter.admin.AdminEventFilterRepository;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilterRepository;
import ru.practicum.ewm.service.event.repository.filter.open.OpenEventFilterRepository;

/**
 * Основной репозиторий для работы с событиями.
 * Объединяет базовые CRUD-операции JPA с кастомными фильтрами для административного, публичного и приватного API.
 *
 * @see JpaRepository
 * @see OpenEventFilterRepository
 * @see AdminEventFilterRepository
 * @see InternalEventFilterRepository
 * @see Event
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>,
        OpenEventFilterRepository,
        AdminEventFilterRepository,
        InternalEventFilterRepository {

    boolean existsByCategoryId(Long id);
}