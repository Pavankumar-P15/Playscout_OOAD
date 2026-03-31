package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.model.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {  
	List<Game> findByCreatedBy_IdOrderByDateDesc(UUID userId);

	Optional<Game> findByIdAndCreatedBy_Id(UUID gameId, UUID userId);

	@Query("SELECT g FROM Game g WHERE g.venue.id = :venueId AND g.date = :date AND (g.status IS NULL OR g.status <> 'CANCELLED')")
	List<Game> findActiveByVenueIdAndDate(@Param("venueId") UUID venueId, @Param("date") LocalDate date);
	
	@Query("SELECT g FROM Game g WHERE g.date >= CURRENT_DATE AND (g.status IS NULL OR g.status <> 'CANCELLED') AND g.venue.disabled = false ORDER BY g.date ASC")
	List<Game> findUpcomingGames();
	
	@Query("SELECT g FROM Game g WHERE g.createdBy.id = :userId AND g.date >= CURRENT_DATE AND (g.status IS NULL OR g.status <> 'CANCELLED') AND g.venue.disabled = false ORDER BY g.date ASC")
	List<Game> findUpcomingGamesByUserId(@Param("userId") UUID userId);
} 
