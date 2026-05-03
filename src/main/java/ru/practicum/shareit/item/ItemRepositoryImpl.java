package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exeption.NotFoundException;
import ru.practicum.shareit.user.FakeUserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, ItemDto> items = new HashMap<>();
    private Long nextId = 1L;
    private final ItemMapper mapper = new ItemMapper();
    private final FakeUserRepository userRepository = new FakeUserRepository();

    @Override
    public List<ItemDto> findByUserId(long userId) {
        return items.values().stream().filter(item -> item.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public ItemDto save(ItemDto item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }

        if (item.getAvailable() == null) {
            throw new IllegalArgumentException("статус вещи должен быть заполнен");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("имя вещи должно быть заполнено");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new IllegalArgumentException("описание вещи должно быть заполнено");
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        ItemDto item = items.get(itemId);
        if (item != null && item.getUserId() == userId) {
            items.remove(itemId);
        }
    }

    @Override
    public ItemDto updateItem(Long userId, long itemId, ItemDto item) {
        ItemDto oldItem = items.get(itemId);
        if (oldItem == null) {
            throw new NotFoundException("Вещь не найдена");
        }

        userRepository.findUserById(userId);

        if (!oldItem.getUserId().equals(userId)) {
            throw new NotFoundException("Обновлять может только владелец");
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }

        return oldItem;
    }

    @Override
    public ItemDto getItem(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String query = text.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item ->
                        containsIgnoreCase(item.getName(), query) ||
                                containsIgnoreCase(item.getDescription(), query))
                .toList();
    }

    private boolean containsIgnoreCase(String source, String query) {
        return source != null && source.toLowerCase().contains(query);
    }
}
