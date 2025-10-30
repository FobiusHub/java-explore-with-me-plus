package ru.practicum.ewm.service.event.repository.PublicEventFilter;


import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.PublicEventFilter.PublicEventFilter;

import java.util.List;

/**
 * Репозиторий для фильтрации событий в публичном API.
 * Предоставляет метод для поиска событий с применением различных фильтров.
 *
 * @see PublicEventFilter
 * @see Event
 */
public interface PublicEventFilterRepository {

    /**
     * Находит все события, соответствующие заданным критериям фильтрации.
     * Использует Criteria API для динамического построения запросов.
     *
     * @param publicEventFilter объект с параметрами фильтрации
     * @return список событий, удовлетворяющих условиям фильтра
     *
     * @see PublicEventFilter
     */
    List<Event> findAllByFilter(PublicEventFilter publicEventFilter);
}