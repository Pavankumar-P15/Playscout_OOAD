package com.example.backend.service;

import com.example.backend.dto.CreateGameRequest;
import com.example.backend.dto.GameResponse;
import com.example.backend.model.Game;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.GameRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private static final DateTimeFormatter SLOT_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    public List<GameResponse> getGameList() {
        return gameRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<GameResponse> getMyGames(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return gameRepository.findByCreatedBy_IdOrderByDateDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GameResponse createGame(String email, CreateGameRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getVenueId() == null) {
            throw new IllegalArgumentException("Venue id is required");
        }
        if (request.getDate() == null) {
            throw new IllegalArgumentException("Game date is required");
        }
        if (request.getSlot() == null || request.getSlot().isBlank()) {
            throw new IllegalArgumentException("Game slot is required");
        }
        if (request.getTotalMembers() == null || request.getTotalMembers() <= 0) {
            throw new IllegalArgumentException("Total members must be greater than 0");
        }
        if (request.getMembersJoined() == null || request.getMembersJoined() <= 0) {
            throw new IllegalArgumentException("Members joined must be greater than 0");
        }
        if (request.getMembersJoined() > request.getTotalMembers()) {
            throw new IllegalArgumentException("Members joined cannot exceed total members");
        }
        if (request.getSkillLevel() == null || request.getSkillLevel().isBlank()) {
            throw new IllegalArgumentException("Skill level is required");
        }

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));

        LocalTime[] parsedSlot = parseSlot(request.getSlot());
        LocalTime startTime = parsedSlot[0];
        LocalTime endTime = parsedSlot[1];

        ensureNoSlotConflict(venue.getId(), request.getDate(), startTime, endTime);

        Game game = new Game();
        game.setCreatedBy(user);
        game.setVenue(venue);
        game.setDate(request.getDate());
        game.setStartTime(startTime);
        game.setEndTime(endTime);
        game.setMembersJoined(request.getMembersJoined());
        game.setTotalMembers(request.getTotalMembers());
        game.setSkillLevel(request.getSkillLevel().trim());

        Game saved = gameRepository.save(game);
        return toResponse(saved);
    }

    public void removeGame(String email, UUID gameId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Game game = gameRepository.findByIdAndCreatedBy_Id(gameId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        gameRepository.delete(game);
    }

    private LocalTime[] parseSlot(String slot) {
        String[] parts = slot.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid slot format. Expected H:mm-H:mm");
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(parts[0].trim(), SLOT_FORMATTER);
            end = LocalTime.parse(parts[1].trim(), SLOT_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid slot format. Expected H:mm-H:mm");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Slot end time must be after start time");
        }

        return new LocalTime[] { start, end };
    }

    private void ensureNoSlotConflict(UUID venueId, LocalDate gameDate, LocalTime startTime, LocalTime endTime) {
        List<Game> games = gameRepository.findByVenue_IdAndDate(venueId, gameDate);

        boolean hasConflict = games.stream()
                .anyMatch(existing -> {
                    return startTime.isBefore(existing.getEndTime()) && existing.getStartTime().isBefore(endTime);
                });

        if (hasConflict) {
            throw new IllegalArgumentException("Selected slot already has a game at this venue");
        }
    }

    private GameResponse toResponse(Game game) {
        Venue venue = game.getVenue();
        return new GameResponse(
                game.getId(),
                game.getDate(),
            game.getStartTime().format(SLOT_FORMATTER) + "-" + game.getEndTime().format(SLOT_FORMATTER),
            venue.getGameIcon(),
            venue.getSport(),
                game.getCreatedBy().getId(),
                game.getMembersJoined(),
                game.getTotalMembers(),
                game.getSkillLevel(),
            venue.getCourtName(),
            venue.getCourtLocation(),
                game.getCreatedBy().getName(),
                game.getCreatedBy().getUserImage() != null
                        ? game.getCreatedBy().getUserImage()
                        : "avatars/m_avatar2.png");
    }

}
