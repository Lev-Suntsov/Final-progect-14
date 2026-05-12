package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id;
    private Timestamp start;
    private Timestamp end;
    private BookingStatus status;
    private long itemId;
    private long userId;
}
