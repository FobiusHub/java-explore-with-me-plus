package ru.practicum.ewm.service.event.repository.filter.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import ru.practicum.ewm.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AdminEventFilterRepositoryImpl implements AdminEventFilterRepository {

    private final EntityManager entityManager;

    public List<Event> findAllByFilter(@NotNull AdminEventFilter adminEventFilter) {
        Class<Event> entityClass = Event.class;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(entityClass);
        Root<Event> root = query.from(entityClass);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация по списку id-пользователей, чьи события нужно найти
        if (adminEventFilter.users() != null && !adminEventFilter.users().isEmpty()) {
            List<Long> users = adminEventFilter.users();
            Predicate predicate = root.get("initiator").get("id").in(users);
            predicates.add(predicate);
        }

        // Фильтрация по списку состояний в которых находятся искомые события
        if (adminEventFilter.states() != null && !adminEventFilter.states().isEmpty()) {
            List<String> states = adminEventFilter.states();
            Predicate predicate = root.get("state").in(states);
            predicates.add(predicate);
        }

        // Фильтрация по категориям по id-списку категорий
        if (adminEventFilter.categories() != null && !adminEventFilter.categories().isEmpty()) {
            List<Long> categories = adminEventFilter.categories();
            Predicate predicate = root.get("category").get("id").in(categories);
            predicates.add(predicate);
        }

        // Фильтрация по дате события (начало периода)
        if (adminEventFilter.rangeStart() != null) {
            LocalDateTime value = adminEventFilter.rangeStart();
            Expression<LocalDateTime> expression = root.get("eventDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, value));
        }

        // Фильтрация по дате события (окончание периода)
        if (adminEventFilter.rangeEnd() != null) {
            LocalDateTime value = adminEventFilter.rangeEnd();
            Expression<LocalDateTime> expression = root.get("eventDate");
            predicates.add(criteriaBuilder.lessThanOrEqualTo(expression, value));
        }

        // Пагинация
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);

        // количество событий, которые нужно пропустить для формирования текущего набора
        if (adminEventFilter.from() != null) {
            typedQuery.setFirstResult(adminEventFilter.from());
        }

        // количество событий в наборе
        if (adminEventFilter.size() != null) {
            typedQuery.setMaxResults(adminEventFilter.size());
        }

        query.where(predicates.toArray(Predicate[]::new));

        return typedQuery.getResultList();
    }
}
