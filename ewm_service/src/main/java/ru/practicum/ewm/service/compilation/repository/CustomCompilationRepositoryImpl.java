package ru.practicum.ewm.service.compilation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.QCompilation;
import ru.practicum.ewm.service.compilation.util.QuerydslUtils;

import java.util.List;

@Repository
public class CustomCompilationRepositoryImpl implements CustomCompilationRepository {

    private final JPAQueryFactory queryFactory;

    public CustomCompilationRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Compilation> findCompilations(Boolean pinned, Pageable pageable) {
        QCompilation compilation = QCompilation.compilation;

        BooleanBuilder builder = new BooleanBuilder();
        if (pinned != null) {
            builder.and(compilation.pinned.eq(pinned));
        }

        PathBuilder<Compilation> entityPath = new PathBuilder<>(Compilation.class, "compilation");
        Sort sort = pageable.getSort().isEmpty() ? Sort.by("id") : pageable.getSort();

        return queryFactory
                .selectFrom(compilation)
                .where(builder)
                .orderBy(QuerydslUtils.toOrderSpecifierArray(sort, entityPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
