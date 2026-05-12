package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Exeption.NotFoundException;
import ru.practicum.shareit.user.UserRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return repository.findByUserId(userId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        userRepository.findUserById(userId);
        itemDto.setUserId(userId);
        return ItemMapper.mapToItemDto(repository.save(ItemMapper.mapToItem(itemDto)));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        repository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, long itemId, ItemDto item) {
        Item existingItem = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        Item updatedItem = repository.save(existingItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.mapToItemDto(repository.getById(itemId));
    }

    @Override
    public List<ItemDto> search(String text) {
        return repository.search(text).stream().map(ItemMapper::mapToItemDto).toList();
    }

}
