package ru.practicum.ewm.service.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.user.model.QUser;
import ru.practicum.ewm.service.user.model.User;

import java.util.List;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final JPAQueryFactory queryFactory;

    public CustomUserRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<User> findUsers(List<Long> ids, int from, int size) {
        QUser user = QUser.user;

        if (ids == null || ids.isEmpty()) {
            return queryFactory
                    .selectFrom(user)
                    .orderBy(user.id.asc())
                    .limit(size)
                    .offset(from)
                    .fetch();
        } else {
            return queryFactory
                    .selectFrom(user)
                    .where(user.id.in(ids))
                    .orderBy(user.id.asc())
                    .limit(size)
                    .offset(from)
                    .fetch();
        }
    }
}
