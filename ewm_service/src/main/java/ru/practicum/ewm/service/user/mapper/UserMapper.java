package ru.practicum.ewm.service.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;
import ru.practicum.ewm.service.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserShortDto toUserShortDto (User user){
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
