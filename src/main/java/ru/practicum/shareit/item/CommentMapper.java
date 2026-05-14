package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toEntity(CommentDto dto) {
        return new Comment(dto.getId(),
                dto.getText(),
                dto.getItem(),
                dto.getAuthor(),
                dto.getCreated()
                );
    }
}
