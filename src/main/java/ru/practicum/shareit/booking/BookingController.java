package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    BookingServiceImpl service;

    @PostMapping
    public BookingDto save(@RequestBody BookingDto dto) {
        return service.save(dto);
    }

    @PatchMapping("/{bookingId}?approved={approved}")
    public BookingDto saveApproved(@PathVariable Long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId) {
        return service.getBooking(bookingId);
    }

    @GetMapping("/{state}")
    public List<BookingDto> getBookingByUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return  service.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner?state={state}/")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        return service.getBookingsByOwner(userId, state);
    }
}
