package com.example.backend.repository;

import com.example.backend.model.Venue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, UUID> {

    List<Venue> findByDisabledFalseOrderByCourtNameAsc();

    List<Venue> findByCreatedByOrderByCourtNameAsc(UUID createdBy);

    Optional<Venue> findByIdAndCreatedBy(UUID id, UUID createdBy);

    Optional<Venue> findByIdAndDisabledFalse(UUID id);
}
