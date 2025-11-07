package ru.practicum.ewm.service.common.commonDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String created;

	private Long event;
	private Long id;
	private Long requester;
	private String status;
}
