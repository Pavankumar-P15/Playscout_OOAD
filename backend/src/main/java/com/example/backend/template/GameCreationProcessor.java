package com.example.backend.template;

import com.example.backend.dto.CreateGameRequest;
import com.example.backend.dto.GameResponse;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;
import com.example.backend.service.GameService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class GameCreationProcessor extends AbstractVenueActionProcessor<CreateGameRequest, GameResponse> {

    private final GameService gameService;

    public GameCreationProcessor(UserRepository userRepository, VenueRepository venueRepository, GameService gameService) {
        super(userRepository, venueRepository);
        this.gameService = gameService;
    }

    @Override
    protected UUID getVenueId(CreateGameRequest request) {
        return request.getVenueId();
    }

    @Override
    protected void checkAvailability(Venue venue, CreateGameRequest request) {
        if (request.getDate() == null) throw new IllegalArgumentException("Game date is required");
        if (request.getSlot() == null || request.getSlot().isBlank()) throw new IllegalArgumentException("Game slot is required");
        if (request.getTotalMembers() == null || request.getTotalMembers() <= 0) throw new IllegalArgumentException("Total members must be greater than 0");
        if (request.getMembersJoined() == null || request.getMembersJoined() <= 0) throw new IllegalArgumentException("Members joined must be greater than 0");
        if (request.getMembersJoined() > request.getTotalMembers()) throw new IllegalArgumentException("Members joined cannot exceed total members");
        if (request.getSkillLevel() == null || request.getSkillLevel().isBlank()) throw new IllegalArgumentException("Skill level is required");
    }

    @Override
    protected GameResponse saveEntity(User user, Venue venue, CreateGameRequest request) {
        return gameService.createGame(user.getEmail(), request);
    }
}