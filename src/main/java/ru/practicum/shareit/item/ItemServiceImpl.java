package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.FakeUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl repository;
    private final FakeUserRepository userRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        userRepository.findUserById(userId);
        itemDto.setUserId(userId);
        return repository.save(itemDto);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        repository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(Long userId, long itemId, ItemDto item) {
        return repository.updateItem(userId, itemId, item);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return repository.getItem(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return repository.search(text);
    }

}
