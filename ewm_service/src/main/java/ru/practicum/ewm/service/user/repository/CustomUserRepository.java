package ru.practicum.ewm.service.user.repository;

import ru.practicum.ewm.service.user.model.User;

import java.util.List;

public interface CustomUserRepository {
    List<User> findUsers(List<Long> ids, int from, int size);
}
