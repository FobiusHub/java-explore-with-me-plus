package ru.practicum.ewm.service.common.commonDto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

	private Long id;
	private String name;
}
