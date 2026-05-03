package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto addNewItem(long userId, ItemDto item);

    void deleteItem(long userId, long itemId);

    ItemDto updateItem(Long userId, long itemId, ItemDto item);

    ItemDto getItem(long itemId);

    List<ItemDto> search(String text);
}
