package ru.practicum.ewm.service.event.repository.filter.open;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class OpenEventFilterRepositoryImpl implements OpenEventFilterRepository {

    private final EntityManager entityManager;

    public List<Event> findAllByFilter(OpenEventFilter publicEventFilter) {
        Class<Event> entityClass = Event.class;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(entityClass);
        Root<Event> root = query.from(entityClass);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация для аннотации по тексту
        if (publicEventFilter.getText() != null && !publicEventFilter.getText().isBlank()) {
            String value = "%" + publicEventFilter.getText().trim().toLowerCase() + "%";
            Expression<String> expression = root.get("annotation");
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(expression), value);
            predicates.add(predicate);
        }

        // Фильтрация по категориям по id-списку категорий
        if (publicEventFilter.getCategories() != null && !publicEventFilter.getCategories().isEmpty()) {
            List<Long> categoryIds = publicEventFilter.getCategories();
            Predicate predicate = root.get("category").get("id").in(categoryIds);
            predicates.add(predicate);
        }

        // Фильтрация по статусу оплаты (true-false)
        if (publicEventFilter.getPaid() != null) {
            Boolean value = publicEventFilter.getPaid();
            Expression<Boolean> expression = root.get("paid");
            Predicate predicate = criteriaBuilder.equal(expression, value);
            predicates.add(predicate);
        }

        // Фильтрация по дате события (начало периода)
        if (publicEventFilter.getRangeStart() != null) {
            LocalDateTime value = publicEventFilter.getRangeStart();
            Expression<LocalDateTime> expression = root.get("eventDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, value));
        }

        // Фильтрация по дате события (окончание периода)
        if (publicEventFilter.getRangeEnd() != null) {
            LocalDateTime value = publicEventFilter.getRangeEnd();
            Expression<LocalDateTime> expression = root.get("eventDate");
            predicates.add(criteriaBuilder.lessThanOrEqualTo(expression, value));
        }

        // Фильтрация по лимиту запросов на участие в событии
        if (publicEventFilter.getOnlyAvailable() != null) {
            Boolean value = publicEventFilter.getOnlyAvailable();
            Expression<Integer> expression = root.get("participantLimit");
            Integer participantLimit = 0;
            Predicate predicate;
            if (value) {
                predicate = criteriaBuilder.greaterThan(expression, participantLimit);
            } else {
                predicate = criteriaBuilder.equal(expression, participantLimit);
            }
            predicates.add(predicate);
        }

        if (!predicates.isEmpty()) {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        // Сортировка...
        if (publicEventFilter.getSort() != null) {
            switch (publicEventFilter.getSort()) {
                // ...по дате события
                case EVENT_DATE:
                    query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
                    break;
                // ...по количеству просмотров
                case VIEWS:
                    query.orderBy(criteriaBuilder.desc(root.get("views")));
                    break;
            }
        } else {
            // ...по умолчанию (по дате события), если не указана
            query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
        }

        // Пагинация
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);

        // количество событий, которые нужно пропустить для формирования текущего набора
        if (publicEventFilter.getFrom() != null) {
            typedQuery.setFirstResult(publicEventFilter.getFrom());
        }

        // количество событий в наборе
        if (publicEventFilter.getSize() != null) {
            typedQuery.setMaxResults(publicEventFilter.getSize());
        }

        return typedQuery.getResultList();
    }
}