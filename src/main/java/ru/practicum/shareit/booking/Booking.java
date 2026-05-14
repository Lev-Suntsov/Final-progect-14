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

    @Column(name = "start_date")
    private Timestamp start;

    @Column(name = "end_date")
    private Timestamp end;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;


    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "booker_id", nullable = false)
    private Long bookerId;
}
