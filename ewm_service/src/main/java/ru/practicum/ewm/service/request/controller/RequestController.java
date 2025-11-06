package ru.practicum.ewm.service.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
