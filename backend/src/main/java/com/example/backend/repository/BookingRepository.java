package com.example.backend.repository;

import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.RefundStatus;
import com.example.backend.model.Booking;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserIdAndStatusNot(UUID userId, BookingStatus excludedStatus);

    Optional<Booking> findFirstByUserIdAndVenueIdAndBookingDateAndBookingSlotAndStatus(
        UUID userId,
        UUID venueId,
        String bookingDate,
        String bookingSlot,
        BookingStatus status
    );

    Optional<Booking> findFirstByOrderId(String orderId);

    List<Booking> findByRefundStatusOrderByRefundRequestedAtDesc(RefundStatus refundStatus);
}
