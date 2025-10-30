package ru.practicum.ewm.service.user.service;

import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(long userId);

    void checkUserExist(long userId);
}
