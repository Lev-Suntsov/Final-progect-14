package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exeption.NotFoundException;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;

    @Override
    @Transactional
    public BookingDto save(BookingDto dto) {
        return BookingMapper.toDto(repository.save(BookingMapper.fromDto(dto)));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getUserId().equals(userId)) {
            throw new NotFoundException("Только владелец вещи может подтверждать бронирование");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toDto(repository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        return BookingMapper.toDto(repository.getById(bookingId));
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        List<Booking> bookings = switch (state) {
            case ALL -> repository.findAllByBookerId(userId);
            case CURRENT -> repository.findCurrentByBookerId(userId, now);
            case PAST -> repository.findPastByBookerId(userId, now);
            case FUTURE -> repository.findFutureByBookerId(userId, now);
            case WAITING -> repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED ->  repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        };
        return  bookings.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, BookingState state) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        List<Booking> bookings = switch (state) {
            case ALL -> repository.findOwnerBookings(userId);
            case CURRENT -> repository.findCurrentOwnerBookings(userId, now);
            case PAST -> repository.findPastOwnerBookings(userId, now);
            case FUTURE -> repository.findFutureOwnerBookings(userId, now);
            case WAITING -> repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        };
        return bookings.stream().map(BookingMapper::toDto).toList();
    }
}
