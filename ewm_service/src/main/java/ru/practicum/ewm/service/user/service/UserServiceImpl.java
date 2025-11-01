package ru.practicum.ewm.service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.common.exception.InternalServerException;
import ru.practicum.ewm.service.common.exception.NotFoundException;
import ru.practicum.ewm.service.common.exception.ValidationException;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.mapper.UserMapper;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        String email = userDto.getEmail();
        validateEmailUnique(email);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        return userRepository.findUsers(ids, from, size).stream().map(UserMapper::toUserDto).toList();
    }

    @Transactional
    @Override
    public void delete(long userId) {
        checkUserExist(userId);
        userRepository.deleteById(userId);
        userRepository.flush(); //Без flush() удаление может быть отложено до конца транзакции
        if (userRepository.existsById(userId)) {
            log.warn("При удалении пользователя id: {} возникла ошибка", userId);
            throw new InternalServerException("При удалении пользователя id: " + userId + " возникла ошибка");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
    }

    private void validateEmailUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("При проверке email возникла ошибка: email уже существует");
            throw new ValidationException("Email " + email + " уже существует");
        }
    }
}
