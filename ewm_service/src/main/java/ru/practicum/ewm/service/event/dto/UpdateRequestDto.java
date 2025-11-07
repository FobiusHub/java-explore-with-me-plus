package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.service.request.model.RequestStatus;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {
    private Set<Long> requestIds;
    private RequestStatus status;
}
