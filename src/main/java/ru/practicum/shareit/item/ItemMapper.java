package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setUserId(item.getUserId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setComments(item.getComments().stream().map(CommentMapper::toDto).toList());
        return dto;
    }

    static Item mapToItem(ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setAvailable(dto.getAvailable());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setUserId(dto.getUserId());
        item.setComments(dto.getComments().stream().map(CommentMapper::toEntity).toList());
        return item;
    }
}
