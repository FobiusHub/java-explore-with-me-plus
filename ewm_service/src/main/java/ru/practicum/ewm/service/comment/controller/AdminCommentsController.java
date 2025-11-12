package ru.practicum.ewm.service.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.comment.service.CommentService;
import ru.practicum.ewm.service.comment.dto.CommentFullDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {

    CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return commentService.getAllCommentsByEventAdmin(eventId);
    }

    @DeleteMapping
    public CommentFullDto deleteComment(Long eventId, Long commentId){
    return commentService.deleteCommentByAdmin(eventId, commentId);
    }

}
