package com.example.backend.service;

import com.example.backend.dto.GameResponse;
import com.example.backend.model.Game;
import com.example.backend.model.User;
import com.example.backend.repository.GameRepository;
import com.example.backend.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {
    
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public List<GameResponse> getGameList() {
        List<Game> games = gameRepository.findAll();

        Set<UUID> createdByIds = games.stream()
                .map(Game::getCreatedBy)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<UUID, User> userById = userRepository.findAllById(createdByIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return games.stream()
                .map(game -> toGameResponse(game, userById.get(game.getCreatedBy())))
                .toList();
    }

    private GameResponse toGameResponse(Game game, User createdByUser) {
        String userName = createdByUser != null ? createdByUser.getName() : "PlayScout User";
        String userImage = createdByUser != null && createdByUser.getUserImage() != null
                ? createdByUser.getUserImage()
                : "avatars/m_avatar2.png";

        return new GameResponse(
            game.getId(),
            game.getDate(),
            game.getFilterDate(),
            game.getSportIcon(),
            game.getSportName(),
            game.getCreatedBy(),
            game.getMembersJoined(),
            game.getTotalMembers(),
            game.getSkillLevel(),
            game.getCourtName(),
            game.getLocation(),
            userName,
            userImage);
    }

}
