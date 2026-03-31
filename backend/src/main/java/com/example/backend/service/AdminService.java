package com.example.backend.service;

import com.example.backend.dto.AdminDashboardResponse;
import com.example.backend.dto.AdminGameResponse;
import com.example.backend.dto.AdminUserResponse;
import com.example.backend.dto.AdminVenueResponse;
import com.example.backend.model.Game;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.GameRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final GameRepository gameRepository;
    private final BookingRepository bookingRepository;

    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalVenues = venueRepository.count();
        long totalGames = gameRepository.count();
        long totalBookings = bookingRepository.count();

        return new AdminDashboardResponse(totalUsers, totalVenues, totalGames, totalBookings);
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            AdminUserResponse response = new AdminUserResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setSuspended(user.getSuspended() != null ? user.getSuspended() : false);
            return response;
        }).collect(Collectors.toList());
    }

    public List<AdminVenueResponse> getAllVenues() {
        return venueRepository.findAll().stream().map(venue -> {
            AdminVenueResponse response = new AdminVenueResponse();
            response.setId(venue.getId());
            response.setCourtName(venue.getCourtName());
            response.setSport(venue.getSport());
            response.setCourtLocation(venue.getCourtLocation());
            response.setCreatedBy(venue.getCreatedBy());
            response.setDisabled(venue.getDisabled() != null ? venue.getDisabled() : false);
            response.setPrice(venue.getPrice());

            // Fetch manager name
            if (venue.getCreatedBy() != null) {
                userRepository.findById(venue.getCreatedBy()).ifPresent(user -> 
                    response.setManagerName(user.getName())
                );
            }
            
            return response;
        }).collect(Collectors.toList());
    }

    public List<AdminGameResponse> getRecentGames(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "date"));
        return gameRepository.findAll(pageable).stream().map(game -> {
            AdminGameResponse response = new AdminGameResponse();
            response.setId(game.getId());
            response.setCourtName(game.getVenue().getCourtName());
            response.setSport(game.getVenue().getSport());
            response.setDate(game.getDate());
            response.setStartTime(game.getStartTime());
            response.setEndTime(game.getEndTime());
            response.setCreatorName(game.getCreatedBy().getName());
            response.setCreatedById(game.getCreatedBy().getId());
            response.setStatus(game.getStatus() != null ? game.getStatus() : "ACTIVE");
            response.setTotalMembers(game.getTotalMembers());
            response.setMembersJoined(game.getMembersJoined());
            return response;
        }).collect(Collectors.toList());
    }

    public void suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSuspended(!user.getSuspended());
        userRepository.save(user);
    }

    public void disableVenue(UUID venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
        venue.setDisabled(!venue.getDisabled());
        venueRepository.save(venue);
    }

    public void cancelGame(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if ("CANCELLED".equals(game.getStatus())) {
            throw new IllegalArgumentException("Game is already cancelled");
        }
        game.setStatus("CANCELLED");
        gameRepository.save(game);
    }
}
