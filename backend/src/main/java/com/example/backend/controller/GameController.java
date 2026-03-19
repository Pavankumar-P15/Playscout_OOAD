package com.example.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.GameService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    
    private final GameService gameService;

    @GetMapping("/game-list")
    public ResponseEntity<Map<String, Object>> getGameList() {
        return ResponseEntity.ok(Map.of("success", true, "data", gameService.getGameList()));
    }
}
