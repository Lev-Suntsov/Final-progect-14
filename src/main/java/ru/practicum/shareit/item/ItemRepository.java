package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    List<ItemDto> findByUserId(long userId);

    ItemDto save(ItemDto item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(Long userId, long itemId, ItemDto item);

    ItemDto getItem(long itemId);

    List<ItemDto> search(String text);
}