package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;

    private String text;

    private Item item;

    private User author;

    private String authorName;

    private Timestamp created;
}
