package com.example.backend.template;

import com.example.backend.dto.BookingRequest;
import com.example.backend.model.Booking;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;
import com.example.backend.service.BookingService;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class BookingProcessor extends AbstractVenueActionProcessor<BookingRequest, Booking> {

    private final BookingService bookingService;

    public BookingProcessor(UserRepository userRepository, VenueRepository venueRepository, BookingService bookingService) {
        super(userRepository, venueRepository);
        this.bookingService = bookingService;
    }

    @Override
    protected UUID getVenueId(BookingRequest request) {
        try {
            return UUID.fromString(request.getVenueId());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void checkAvailability(Venue venue, BookingRequest request) {
        if (request.getBookingDate() == null) {
            throw new IllegalArgumentException("Booking date is required");
        }
        if (request.getBookingSlot() == null || request.getBookingSlot().isBlank()) {
            throw new IllegalArgumentException("Booking slot is required");
        }
        validateSlotFormat(request.getBookingSlot());
    }

    private void validateSlotFormat(String bookingSlot) {
        if (bookingSlot == null || bookingSlot.isBlank()) {
            throw new IllegalArgumentException("Booking slot is required");
        }
        String[] parts = bookingSlot.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid booking slot format: " + bookingSlot);
        }
        parseTime(parts[0].trim(), bookingSlot);
        parseTime(parts[1].trim(), bookingSlot);
    }

    private LocalTime parseTime(String raw, String bookingSlot) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm")
        };
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(raw, formatter);
            } catch (Exception e) {
                // Try next formatter
            }
        }
        throw new IllegalArgumentException("Invalid booking slot format: " + bookingSlot);
    }

    @Override
    protected Booking saveEntity(User user, Venue venue, BookingRequest request) {
        return bookingService.createBooking(request);
    }
}