package ru.practicum.ewm.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя должно быть заполнено")
    @Size(min = 2, max = 250)
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    @Size(min = 6, max = 254)
    private String email;
}
