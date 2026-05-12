package ru.practicum.shareit.item;

public class ItemMapper {
    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setUserId(item.getUserId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public Item mapToItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setAvailable(dto.getAvailable());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setUserId(dto.getUserId());
        return item;
    }
}
