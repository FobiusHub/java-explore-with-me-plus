package ru.practicum.ewm.service.common.commonDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NewUserRequest {
    @Email(message = "Некорректный email")
    private String email;
    @NotBlank
    private String name;
}
