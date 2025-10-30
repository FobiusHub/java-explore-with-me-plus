package ru.practicum.ewm.service.common.commonDto;

import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {
    @Positive
    private Long id;

    @Size(min = 1, message = "Название категории не должно быть меньше 1 символа")
    @Size(max = 50, message = "Название категории не должно быть больше 50 символов")
    private String name;

    public CategoryDto toCategoryDto() {
        return CategoryDto.builder()
                .id(getId())
                .name(getName())
                .build();
    }
}
