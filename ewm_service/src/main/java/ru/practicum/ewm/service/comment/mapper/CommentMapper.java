package ru.practicum.ewm.service.comment.mapper;

import ru.practicum.ewm.service.comment.dto.CommentFullDto;
import ru.practicum.ewm.service.comment.dto.CommentShortDto;
import ru.practicum.ewm.service.comment.model.Comment;
import ru.practicum.ewm.service.event.mapper.EventMapper;

public class CommentMapper {

    public static CommentShortDto toShortDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentShortDto.builder()
                .text(comment.getText())
                .commentator(comment.getCommentator())
                .publishedOn(comment.getPublishedOn())
                .build();
    }

    public static CommentFullDto toFullDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentator(comment.getCommentator() == null ? null : EventMapper.toUserShort(comment.getCommentator()))
                .publishedOn(comment.getPublishedOn())
                .deleted(comment.isDeleted())
                .build();
    }
}