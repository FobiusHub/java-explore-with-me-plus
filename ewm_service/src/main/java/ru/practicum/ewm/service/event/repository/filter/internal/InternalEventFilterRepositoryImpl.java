package ru.practicum.ewm.service.event.repository.filter.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.model.Event;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class InternalEventFilterRepositoryImpl implements InternalEventFilterRepository {

    private final EntityManager entityManager;

    public List<Event> findAllByFilter(@NotNull InternalEventFilterDto internalEventFilter) {
        Class<Event> entityClass = Event.class;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(entityClass);
        Root<Event> root = query.from(entityClass);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация по id-пользователя, чьи события нужно найти
        if (internalEventFilter.userId() != null) {
            Long userId = internalEventFilter.userId();
            Predicate predicate = criteriaBuilder.equal(root.get("initiator").get("id"), userId);
            predicates.add(predicate);
        }

        query.select(root)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(criteriaBuilder.asc(root.get("id")));

        // Пагинация
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);

        // количество событий, которые нужно пропустить для формирования текущего набора
        if (internalEventFilter.from() != null) {
            typedQuery.setFirstResult(internalEventFilter.from());
        }

        // количество событий в наборе
        if (internalEventFilter.size() != null) {
            typedQuery.setMaxResults(internalEventFilter.size());
        }

        return typedQuery.getResultList();
    }
}
