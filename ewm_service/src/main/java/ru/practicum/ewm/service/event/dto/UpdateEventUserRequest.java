package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.service.event.enums.StateActionInternal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {
    private StateActionInternal stateAction;
}
