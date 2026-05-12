package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto dto);

    BookingDto approveBooking(Long bookingId, Long userId, boolean approved);

    BookingDto getBooking(Long bookingId);

    List<BookingDto> getBookingsByUser(Long userId, BookingState state);

    List<BookingDto> getBookingsByOwner(Long userId, BookingState state);
}
