package ru.practicum.ewm.service.event.repository.filter.open;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.event.enums.EventState;
import ru.practicum.ewm.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpenEventFilterRepositoryImpl implements OpenEventFilterRepository {

    private final EntityManager entityManager;

    @Override
    public List<Event> findAllByFilter(OpenEventFilter publicEventFilter) {
        log.info("=== PUBLIC EVENT FILTER START ===");
        log.info("Filter parameters: text='{}', categories={}, paid={}, onlyAvailable={}, sort={}, from={}, size={}",
                publicEventFilter.getText(), publicEventFilter.getCategories(),
                publicEventFilter.getPaid(), publicEventFilter.getOnlyAvailable(),
                publicEventFilter.getSort(), publicEventFilter.getFrom(),
                publicEventFilter.getSize());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        // 1. Обязательный фильтр по статусу PUBLISHED
        predicates.add(criteriaBuilder.equal(root.get("status"), EventState.PUBLISHED));
        log.info("Added status filter: PUBLISHED");

        // 2. Текстовый поиск по аннотации и описанию (без учета регистра)
        if (publicEventFilter.getText() != null && !publicEventFilter.getText().isBlank()) {
            String value = "%" + publicEventFilter.getText().trim().toLowerCase() + "%";

            Predicate annotationPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("annotation")), value);

            Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), value);

            Predicate textPredicate = criteriaBuilder.or(annotationPredicate, descriptionPredicate);
            predicates.add(textPredicate);
            log.info("Added text filter (case-insensitive): '{}'", publicEventFilter.getText());
        }

        // 3. Фильтрация по категориям
        if (publicEventFilter.getCategories() != null && !publicEventFilter.getCategories().isEmpty()) {
            Predicate predicate = root.get("category").get("id").in(publicEventFilter.getCategories());
            predicates.add(predicate);
            log.info("Added categories filter: {}", publicEventFilter.getCategories());
        }

        // 4. Фильтрация по оплате
        if (publicEventFilter.getPaid() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("paid"), publicEventFilter.getPaid());
            predicates.add(predicate);
            log.info("Added paid filter: {}", publicEventFilter.getPaid());
        }

        // 5. Фильтрация по датам
        LocalDateTime now = LocalDateTime.now();
        if (publicEventFilter.getRangeStart() != null && publicEventFilter.getRangeEnd() != null) {
            // Если указан диапазон дат
            Predicate startPredicate = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("eventDate"), publicEventFilter.getRangeStart());
            Predicate endPredicate = criteriaBuilder.lessThanOrEqualTo(
                    root.get("eventDate"), publicEventFilter.getRangeEnd());
            predicates.add(criteriaBuilder.and(startPredicate, endPredicate));
            log.info("Added date range filter: {} to {}",
                    publicEventFilter.getRangeStart(), publicEventFilter.getRangeEnd());
        } else if (publicEventFilter.getRangeStart() != null) {
            // Если указана только начальная дата
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("eventDate"), publicEventFilter.getRangeStart()));
            log.info("Added rangeStart filter: {}", publicEventFilter.getRangeStart());
        } else if (publicEventFilter.getRangeEnd() != null) {
            // Если указана только конечная дата
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("eventDate"), publicEventFilter.getRangeEnd()));
            log.info("Added rangeEnd filter: {}", publicEventFilter.getRangeEnd());
        } else {
            // Если диапазон не указан - события в будущем
            predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), now));
            log.info("Added default date filter: eventDate > now ({})", now);
        }

        // 6. Фильтрация по доступности
        if (publicEventFilter.getOnlyAvailable() != null && publicEventFilter.getOnlyAvailable()) {
            // Только события с доступными местами
            Predicate unlimited = criteriaBuilder.equal(root.get("participantLimit"), 0);
            Predicate hasAvailableSpots = criteriaBuilder.greaterThan(
                    root.get("participantLimit"), root.get("confirmedRequests"));
            predicates.add(criteriaBuilder.or(unlimited, hasAvailableSpots));
            log.info("Added onlyAvailable filter: true");
        }

        // Применяем все предикаты
        if (!predicates.isEmpty()) {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            log.info("Applied {} predicates total", predicates.size());
        }

        // 7. Сортировка
        if (publicEventFilter.getSort() != null) {
            switch (publicEventFilter.getSort()) {
                case EVENT_DATE:
                    query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
                    log.info("Applied sort: EVENT_DATE");
                    break;
                case VIEWS:
                    query.orderBy(criteriaBuilder.desc(root.get("views")));
                    log.info("Applied sort: VIEWS");
                    break;
            }
        } else {
            query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
            log.info("Applied default sort: EVENT_DATE");
        }

        // 8. Пагинация
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);

        if (publicEventFilter.getFrom() != null) {
            typedQuery.setFirstResult(publicEventFilter.getFrom());
            log.info("Applied offset: {}", publicEventFilter.getFrom());
        }

        if (publicEventFilter.getSize() != null) {
            typedQuery.setMaxResults(publicEventFilter.getSize());
            log.info("Applied limit: {}", publicEventFilter.getSize());
        }

        List<Event> result = typedQuery.getResultList();
        log.info("=== PUBLIC EVENT FILTER END: Found {} events ===", result.size());

        if (!result.isEmpty()) {
            log.info("Found events with IDs: {}",
                    result.stream().map(Event::getId).toList());
        }
        return result;
    }
}