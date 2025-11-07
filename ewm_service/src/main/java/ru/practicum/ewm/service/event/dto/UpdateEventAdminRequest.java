package ru.practicum.ewm.service.event.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.service.event.enums.StateActionAdmin;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {
    StateActionAdmin stateAction;
}

