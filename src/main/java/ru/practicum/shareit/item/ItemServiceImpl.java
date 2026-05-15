package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserServiceImpl userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {

        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available не может быть пустым");
        }

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }

        userService.findUserById(userId);
        itemDto.setUserId(userId);

        Item item = ItemMapper.mapToItem(itemDto);
        return ItemMapper.mapToItemDto(repository.save(item));
    }

    @Transactional
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
    @Transactional(readOnly = true)
    public ItemDto getItem(long itemId, long requesterId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();

        ItemDto dto = ItemMapper.mapToItemDto(item);
        dto.setComments(comments);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (item.getUserId().equals(requesterId)) {
            bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, now)
                    .ifPresent(booking -> dto.setLastBooking(BookingMapper.toDtoForOut(booking)));
            // при необходимости сюда можно добавить nextBooking, если есть отдельный метод
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {

        List<Item> items = repository.findAllByUserIdOrderByIdAsc(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<CommentDto>> commentsByItemId = commentRepository
                .findAllByItem_IdInOrderByItem_IdAscCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        List<BookingDtoOut> bookings = bookingRepository
                .findAllByItemIdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toDtoForOut)
                .toList();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        Map<Long, BookingDtoOut> lastBookingByItemId = new HashMap<>();
        Map<Long, BookingDtoOut> nextBookingByItemId = new HashMap<>();

        for (BookingDtoOut booking : bookings) {
            Long itemId = booking.getItem().getId();

            if (!booking.getStart().after(now)) {
                lastBookingByItemId.put(itemId, booking);
            } else {
                nextBookingByItemId.putIfAbsent(itemId, booking);
            }
        }

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.mapToItemDto(item);
                    dto.setComments(commentsByItemId.getOrDefault(item.getId(), List.of()));
                    dto.setLastBooking(lastBookingByItemId.get(item.getId()));
                    dto.setNextBooking(nextBookingByItemId.get(item.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return repository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto comment) {
        if (itemId == null) {
            throw new IllegalArgumentException("id вещи не может быть пустым");
        }
        if (userId == null) {
            throw new IllegalArgumentException("id пользователя не может быть пустым");
        }

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        UserDto userDto = userService.findUserById(userId);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        boolean hasBooking = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId,
                        userId,
                        BookingStatus.APPROVED,
                        now
                );

        if (!hasBooking) {
            throw new IllegalArgumentException(
                    "Пользователь не арендовал вещь или аренда ещё не завершена"
            );
        }

        comment.setItem(item);
        comment.setAuthor(UserMapper.mapToUser(userDto));
        comment.setCreated(now);

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toEntity(comment)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getItemById(Long userId, Long itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> findAllByIds(Set<Long> ids){
        return  repository.findAllById(ids).stream().map(ItemMapper::mapToItemDto).toList();
    }
}
