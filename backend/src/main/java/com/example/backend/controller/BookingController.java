package com.example.backend.controller;

import com.example.backend.dto.CreateBookingRequest;
import com.example.backend.dto.BookingResponse;
import com.example.backend.dto.UpdateBookingStatusRequest;
import com.example.backend.enums.BookingStatus;
import com.example.backend.service.BookingService;
import java.util.UUID;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addBooking(
            Authentication authentication,
            @RequestBody CreateBookingRequest request) {
        try {
            BookingResponse booking = bookingService.createBooking(authentication.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Booking created successfully", "data", booking));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listBookings(Authentication authentication) {
        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", bookingService.listBookings(authentication.getName())));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            Authentication authentication,
            @RequestBody UpdateBookingStatusRequest request,
            @PathVariable UUID id) {
        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Booking status is required"));
        }

        if (request.getStatus() != BookingStatus.CANCELLED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Only cancellation is supported"));
        }

        try {
            bookingService.cancelBooking(authentication.getName(), id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Booking cancelled successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
