package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.backend.enums.BookingStatus;
import com.example.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    List<Booking> findByVenue_IdAndBookingDateAndStatusIn(
        UUID venueId,
        LocalDate bookingDate,
        List<BookingStatus> statuses
    );

    Optional<Booking> findByIdAndUser_Id(UUID bookingId, UUID userId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status != 'CANCELLED' AND b.bookingDate >= CURRENT_DATE ORDER BY b.bookingDate ASC")
    List<Booking> findUpcomingBookings(@Param("userId") UUID userId);
}
