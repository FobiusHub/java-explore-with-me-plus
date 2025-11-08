package ru.practicum.ewm.service.event.repository.filter.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AdminEventFilterRepositoryImpl implements AdminEventFilterRepository {

    private final EntityManager entityManager;

    public List<Event> findAllByFilter(AdminEventFilter adminEventFilter) {
        Class<Event> entityClass = Event.class;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(entityClass);
        Root<Event> root = query.from(entityClass);

        Join<Event, User> userJoin = root.join("initiator", JoinType.INNER);
        Join<Event, Category> categoryJoin = root.join("category", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация по списку id-пользователей
        if (adminEventFilter.users() != null && !adminEventFilter.users().isEmpty()) {
            List<Long> users = adminEventFilter.users();
            Predicate predicate = userJoin.get("id").in(users);
            predicates.add(predicate);
        }

        // Фильтрация по списку состояний
        if (adminEventFilter.states() != null && !adminEventFilter.states().isEmpty()) {
            List<EventState> eventStates = adminEventFilter.states();
            Predicate predicate = root.get("status").in(eventStates);
            predicates.add(predicate);
        }

        // Фильтрация по категориям
        if (adminEventFilter.categories() != null && !adminEventFilter.categories().isEmpty()) {
            List<Long> categories = adminEventFilter.categories();
            Predicate predicate = categoryJoin.get("id").in(categories);
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

        query.select(root)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(criteriaBuilder.asc(root.get("id")));


        // Применяем предикаты ДО создания запроса
        if (!predicates.isEmpty()) {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        // Создаем запрос
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);

        // Пагинация
        if (adminEventFilter.from() != null) {
            typedQuery.setFirstResult(adminEventFilter.from());
        }

        if (adminEventFilter.size() != null) {
            typedQuery.setMaxResults(adminEventFilter.size());
        }

        return typedQuery.getResultList();
    }
}