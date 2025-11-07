package ru.practicum.ewm.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.repository.filter.internal.InternalEventFilter;
import ru.practicum.ewm.service.event.service.EventServiceInternal;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;

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
            @PathVariable("userId") @Positive Long userId,
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
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(
            @PathVariable("userId") @Positive @NotNull Long userId,
            @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST /users/{}/events with body:{}", userId, newEventDto);
        newEventDto.setInitiator(userId);
        return eventServiceInternal.postEvent(newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventOfUserBy(
            @PathVariable("userId") @Positive @NotNull Long userId,
            @PathVariable("eventId") @Positive @NotNull Long eventId
    ) {
        return eventServiceInternal.getEventOfUserBy(InternalEventFilter.builder()
                .userId(userId)
                .eventId(eventId)
                .build());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventOfUserBy(
            @PathVariable("userId") @Positive @NotNull Long userId,
            @PathVariable("eventId") @Positive @NotNull Long eventId,
            @RequestBody UpdateEventUserRequest updateEvent
    ) {
        updateEvent.setInitiator(userId);
        updateEvent.setEvent(eventId);
        return eventServiceInternal.patchEventOfUserBy(updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public ParticipationRequestDto getRequestOfUserBy(
            @PathVariable("userId") @Positive @NotNull Long userId,
            @PathVariable("eventId") @Positive @NotNull Long eventId
    ) {
        return eventServiceInternal.getRequestOfUserBy(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public void updateRequestStatus(
            @PathVariable("userId") @Positive @NotNull Long userId,
            @PathVariable("eventId") @Positive @NotNull Long eventId,
            @RequestBody @Valid UpdateRequestDto updateRequestDto
    ) {
        eventServiceInternal.updateRequestStatus(updateRequestDto, userId, eventId);
    }
}
