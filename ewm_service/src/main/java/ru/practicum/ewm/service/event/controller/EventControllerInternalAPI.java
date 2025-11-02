package ru.practicum.ewm.service.event.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceInternal;

import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventControllerInternalAPI {

    private final EventServiceInternal eventServiceInternal;

    @GetMapping
    public List<EventShortDto> getEventsOfUserById(
            @PathVariable("id") @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size
    ) {
        return eventServiceInternal.getEventsOfUserBy(InternalEventFilter.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build());
    }

    @PostMapping()
    public EventFullDto postEvent(
            @PathVariable("userId") Long userId,
            @RequestBody @NotNull NewEventDto newEventDto) {
        log.info("GET /users/{}/events with body:{}", userId, newEventDto);
        newEventDto.setInitiatorId(userId);
        return eventServiceInternal.postEvent(newEventDto);
    }
}
