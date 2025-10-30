package ru.practicum.ewm.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventFilterAdmin.AdminEventFilterRepository;
import ru.practicum.ewm.service.event.repository.PublicEventFilter.PublicEventFilterRepository;

/**
 * Основной репозиторий для работы с событиями.
 * Объединяет базовые CRUD-операции JPA с кастомными фильтрами для административного и публичного API.
 *
 * @see JpaRepository
 * @see PublicEventFilterRepository
 * @see AdminEventFilterRepository
 * @see Event
 */
@Repository
public interface EventJpaRepository extends JpaRepository<Event, Long>, PublicEventFilterRepository, AdminEventFilterRepository {
}