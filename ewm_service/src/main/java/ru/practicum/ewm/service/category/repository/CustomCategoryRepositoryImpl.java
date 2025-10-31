package ru.practicum.ewm.service.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.model.QCategory;

import java.util.List;

@Repository
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    private final JPAQueryFactory queryFactory;

    public CustomCategoryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> findCategories(int from, int size) {
        QCategory category = QCategory.category;

        return queryFactory
                .selectFrom(category)
                .orderBy(category.id.asc())
                .limit(size)
                .offset(from)
                .fetch();

    }
}
