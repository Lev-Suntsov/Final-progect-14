package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exeption.NotFoundException;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;
    ItemRepository itemRepository;
    UserServiceImpl userService;
    ItemServiceImpl itemService;

    @Override
    @Transactional
    public BookingDtoOut save(BookingDtoIn dto) {

        if (dto.getItemId() == null) {
            throw new IllegalArgumentException("id вещи не может быть пустым");
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и конца обязательны");
        }

        if (!dto.getStart().before(dto.getEnd())) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getUserId().equals(dto.getBookerId())) {
            throw new NotFoundException("Вещь не может бронировать владелец");
        }

        UserDto user = userService.findUserById(dto.getBookerId());

        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        Booking booking = BookingMapper.fromDto(dto);
        booking.setItemId(item.getId());
        booking.setItemId(item.getId());
        booking.setBookerId(user.getId());
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = repository.save(booking);

        return BookingMapper.toDtoForOut(saved, ItemMapper.mapToItemDto(item), user);
    }

    @Override
    @Transactional
    public BookingDtoOut approveBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        ItemDto item = itemService.getItem(booking.getItemId(), booking.getBookerId());

        if (!item.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Подтверждать бронирование может только владелец вещи");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = repository.save(booking);

        return BookingMapper.toDtoForOut(
                saved,
                item,
                userService.findUserById(saved.getBookerId())
        );

    }

    @Override
    public BookingDtoOut getBooking(Long bookingId) {
        Booking booking = repository.getById(bookingId);
        return BookingMapper.toDtoForOut(booking, itemService.getItem(booking.getItemId(), booking.getBookerId()), userService.findUserById(booking.getBookerId()));
    }

    @Override
    public List<BookingDtoOut> getBookingsByUser(Long userId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        userService.findUserById(userId);
        List<Booking> bookings = switch (state) {
            case ALL -> repository.findAllByBookerId(userId);
            case CURRENT -> repository.findCurrentByBookerId(userId, now);
            case PAST -> repository.findPastByBookerId(userId, now);
            case FUTURE -> repository.findFutureByBookerId(userId, now);
            case WAITING -> repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED ->  repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(booking -> BookingMapper.toDtoForOut(
                        booking,
                        itemService.getItem(booking.getItemId(), booking.getBookerId()),
                        userService.findUserById(booking.getBookerId())
                ))
                .toList();
    }

    @Override
    public List<BookingDtoOut> getBookingsByOwner(Long userId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        userService.findUserById(userId);
        List<Booking> bookings = switch (state) {
            case ALL -> repository.findOwnerBookings(userId);
            case CURRENT -> repository.findCurrentOwnerBookings(userId, now);
            case PAST -> repository.findPastOwnerBookings(userId, now);
            case FUTURE -> repository.findFutureOwnerBookings(userId, now);
            case WAITING -> repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(booking -> BookingMapper.toDtoForOut(
                        booking,
                        itemService.getItem(booking.getItemId(), booking.getBookerId()),
                        userService.findUserById(booking.getBookerId())
                ))
                .toList();
    }
}
