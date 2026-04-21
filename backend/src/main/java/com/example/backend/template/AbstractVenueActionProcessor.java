package com.example.backend.template;

import java.util.UUID;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractVenueActionProcessor<T, R> {

    protected final UserRepository userRepository;
    protected final VenueRepository venueRepository;

    protected AbstractVenueActionProcessor(UserRepository userRepository, VenueRepository venueRepository) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public final R processAction(String userIdentifier, T request) {
        User user = validateUser(userIdentifier, request);
        UUID venueId = getVenueId(request);
        Venue venue = validateVenue(venueId);
        
        checkAvailability(venue, request);
        
        return saveEntity(user, venue, request);
    }

    protected User validateUser(String userIdentifier, T request) {
        if (userIdentifier == null || userIdentifier.isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        return userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    protected Venue validateVenue(UUID venueId) {
        if (venueId == null) {
            throw new IllegalArgumentException("Venue id is required");
        }
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
        
        if (Boolean.TRUE.equals(venue.getDisabled())) {
            throw new IllegalArgumentException("This venue is currently disabled");
        }
        return venue;
    }

    protected abstract UUID getVenueId(T request);

    protected abstract void checkAvailability(Venue venue, T request);

    protected abstract R saveEntity(User user, Venue venue, T request);
}