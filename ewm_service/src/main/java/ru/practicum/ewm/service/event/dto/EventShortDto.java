package ru.practicum.ewm.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.service.common.commonDto.CategoryDto;
import ru.practicum.ewm.service.event.utill.DateTimeFormatUtil;
import ru.practicum.ewm.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
	private String annotation;
	private CategoryDto category;
	private Long confirmedRequests;

	@JsonFormat(pattern = DateTimeFormatUtil.DATE_TIME_FORMAT)
	private LocalDateTime eventDate;

	private UserShortDto initiator;
	private Boolean paid;
	private String title;
	private Long views;
}
