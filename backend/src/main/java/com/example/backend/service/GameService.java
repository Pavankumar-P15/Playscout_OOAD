package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.GameResponse;
import com.example.backend.model.Game;
import com.example.backend.repository.GameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {
    
    private final GameRepository gameRepository;

    public List<GameResponse> getGameList() {
        return gameRepository.findAll().stream().map(this::toGameResponse).toList();
    }

    private GameResponse toGameResponse(Game game) {
        String dummyName = "PlayScout User";
        String dummyImage = "avatars/m_avatar2.png";

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
            dummyName,
            dummyImage);
    }

}
