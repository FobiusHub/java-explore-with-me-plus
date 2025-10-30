package ru.practicum.ewm.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private long id;

    @NotBlank(message = "Имя должно быть заполнено")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    private String email;
}
