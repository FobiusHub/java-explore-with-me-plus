package ru.practicum.ewm.service.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.comment.service.CommentService;
import ru.practicum.ewm.service.comment.dto.CommentShortDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private CommentService commentService;

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return commentService.getAllCommentsByEventPublic(eventId);
    }

    @GetMapping("/{commentId}")
    public CommentShortDto getCommentsOfUser(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        return commentService.getComment(userId, commentId);
    }
}
