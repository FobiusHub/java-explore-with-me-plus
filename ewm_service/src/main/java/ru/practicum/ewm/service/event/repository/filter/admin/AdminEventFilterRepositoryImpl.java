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
        if (adminEventFilter.getUsers() != null && !adminEventFilter.getUsers().isEmpty()) {
            List<Long> users = adminEventFilter.getUsers();
            Predicate predicate = userJoin.get("id").in(users);
            predicates.add(predicate);
        }

        // Фильтрация по списку состояний
        if (adminEventFilter.getStates() != null && !adminEventFilter.getStates().isEmpty()) {
            List<EventState> eventStates = adminEventFilter.getStates();
            Predicate predicate = root.get("status").in(eventStates);
            predicates.add(predicate);
        }

        // Фильтрация по категориям
        if (adminEventFilter.getCategories() != null && !adminEventFilter.getCategories().isEmpty()) {
            List<Long> categories = adminEventFilter.getCategories();
            Predicate predicate = categoryJoin.get("id").in(categories);
            predicates.add(predicate);
        }

        // Фильтрация по дате события (начало периода)
        if (adminEventFilter.getRangeStart() != null) {
            LocalDateTime value = adminEventFilter.getRangeStart();
            Expression<LocalDateTime> expression = root.get("eventDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, value));
        }

        // Фильтрация по дате события (окончание периода)
        if (adminEventFilter.getRangeEnd() != null) {
            LocalDateTime value = adminEventFilter.getRangeEnd();
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
        if (adminEventFilter.getFrom() != null) {
            typedQuery.setFirstResult(adminEventFilter.getFrom());
        }

        if (adminEventFilter.getSize() != null) {
            typedQuery.setMaxResults(adminEventFilter.getSize());
        }

        return typedQuery.getResultList();
    }
}