package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {  
	List<Game> findByCreatedBy_IdOrderByDateDesc(UUID userId);

	Optional<Game> findByIdAndCreatedBy_Id(UUID gameId, UUID userId);

	List<Game> findByVenue_IdAndDate(UUID venueId, LocalDate date);
} 
