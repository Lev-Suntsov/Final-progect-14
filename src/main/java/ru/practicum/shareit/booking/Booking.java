package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp start;

    @Column
    private Timestamp end;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "user_id")
    private Long userId;
}
