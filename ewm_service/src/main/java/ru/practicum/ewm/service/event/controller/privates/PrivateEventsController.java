package ru.practicum.ewm.service.event.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.service.EventService;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {

    private final EventService service;

    @GetMapping public List<EventShortDto> getMy(@PathVariable Long userId,
                                                    @RequestParam(value="from", defaultValue="0") int from,
                                                    @RequestParam(value="size", defaultValue="10") int size) {
        return service.getUserEvents(
                userId,
                PageRequest.of(from/size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable Long userId,
                            @Valid @RequestBody NewEventDto dto) {
        return service.addEvent(
                userId,
                dto
        );
    }

    @GetMapping("/{eventId}")
    public EventFullDto get(@PathVariable Long userId,
                            @PathVariable Long eventId) {
        return service.getUserEvent(
                userId,
                eventId
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest dto) {
        return service.updateEventUser(
                userId,
                eventId,
                dto
        );
    }
}
