package ru.practicum.ewm.service.event.repository.filter.open;


import ru.practicum.ewm.service.event.model.Event;

import java.util.List;

/**
 * Репозиторий для фильтрации событий в публичном API.
 * Предоставляет метод для поиска событий с применением различных фильтров.
 *
 * @see OpenEventFilterDto
 * @see Event
 */
public interface OpenEventFilterRepository {

    /**
     * Находит все события, соответствующие заданным критериям фильтрации.
     * Использует Criteria API для динамического построения запросов.
     *
     * @param publicEventFilter объект с параметрами фильтрации
     * @return список событий, удовлетворяющих условиям фильтра
     *
     * @see OpenEventFilterDto
     */
    List<Event> findAllByFilter(OpenEventFilterDto publicEventFilter);
}