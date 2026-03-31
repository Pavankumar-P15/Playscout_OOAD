package com.example.backend.controller;

import com.example.backend.dto.AdminDashboardResponse;
import com.example.backend.dto.AdminGameResponse;
import com.example.backend.dto.AdminUserResponse;
import com.example.backend.dto.AdminVenueResponse;
import com.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        AdminDashboardResponse stats = adminService.getDashboard();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users
        ));
    }

    @GetMapping("/venues")
    public ResponseEntity<Map<String, Object>> getAllVenues() {
        List<AdminVenueResponse> venues = adminService.getAllVenues();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", venues
        ));
    }

    @GetMapping("/games")
    public ResponseEntity<Map<String, Object>> getRecentGames(
            @RequestParam(defaultValue = "20") int limit) {
        List<AdminGameResponse> games = adminService.getRecentGames(limit);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", games
        ));
    }

    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<Map<String, Object>> suspendUser(@PathVariable UUID userId) {
        try {
            adminService.suspendUser(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User suspension status updated"
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PatchMapping("/venues/{venueId}/disable")
    public ResponseEntity<Map<String, Object>> disableVenue(@PathVariable UUID venueId) {
        try {
            adminService.disableVenue(venueId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Venue status updated"
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PatchMapping("/games/{gameId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelGame(@PathVariable UUID gameId) {
        try {
            adminService.cancelGame(gameId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Game cancelled successfully"
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }
}
