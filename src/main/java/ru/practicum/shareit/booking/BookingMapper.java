package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    public static Booking fromDto(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus());
        booking.setStart(dto.getStart());
        booking.setUserId(dto.getUserId());
        booking.setItemId(dto.getItemId());
        return booking;
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setEnd(booking.getEnd());
        dto.setStart(booking.getStart());
        dto.setItemId(booking.getItemId());
        dto.setStatus(booking.getStatus());
        dto.setUserId(booking.getUserId());
        return dto;
    }
}
