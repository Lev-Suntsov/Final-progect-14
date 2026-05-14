package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto item);

    void deleteItem(long userId, long itemId);

    ItemDto updateItem(Long userId, long itemId, ItemDto item);

    public ItemDto getItem(long itemId, long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);

    List<CommentDto> getItemById(Long userId, Long itemId);

    List<ItemDto> getAllItemsByOwner(Long ownerId);
}
