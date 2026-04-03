package com.example.backend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.CreateVenueRequest;
import com.example.backend.dto.VenueResponse;
import com.example.backend.enums.Role;
import com.example.backend.model.User;
import com.example.backend.model.Venue;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:uploads}")
    private String supabaseBucket;

    public List<VenueResponse> getVenueList() {
        return venueRepository.findByDisabledFalseOrderByCourtNameAsc()
                .stream()
                .map(this::toVenueResponse)
                .toList();
    }

    public List<VenueResponse> getVenueListForManager(String email) {
        User user = getUser(email);
        ensureFacilityManager(user);

        return venueRepository.findByCreatedByOrderByCourtNameAsc(user.getId())
                .stream()
                .map(this::toVenueResponse)
                .toList();
    }

    public VenueResponse createVenue(
            String email,
            CreateVenueRequest request,
            MultipartFile courtImage) {
        User user = getUser(email);
        ensureFacilityManager(user);

        // Validation
        if (request.getCourtName() == null || request.getCourtName().isBlank()) {
            throw new IllegalArgumentException("Court name is required");
        }
        if (request.getSport() == null || request.getSport().isBlank()) {
            throw new IllegalArgumentException("Sport is required");
        }
        if (request.getCourtLocation() == null || request.getCourtLocation().isBlank()) {
            throw new IllegalArgumentException("Court location is required");
        }
        if (request.getCourtsAvailable() == null || request.getCourtsAvailable() <= 0) {
            throw new IllegalArgumentException("Courts available must be greater than 0");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new IllegalArgumentException("Price must be 0 or more");
        }
        if (courtImage == null || courtImage.isEmpty()) {
            throw new IllegalArgumentException("Court image is required");
        }

        Venue venue = new Venue();
        venue.setCourtName(request.getCourtName().trim());
        venue.setSport(request.getSport().trim());
        venue.setCourtLocation(request.getCourtLocation().trim());
        venue.setCourtsAvailable(request.getCourtsAvailable());
        venue.setPrice(request.getPrice());
        venue.setCourtImage(uploadImageToSupabase(courtImage));
        venue.setGameIcon(resolveGameIcon(request.getSport()));
        venue.setRating(BigDecimal.ZERO);
        venue.setCreatedBy(user.getId());

        return toVenueResponse(venueRepository.save(venue));
    }

    public void deleteVenue(String email, UUID venueId) {
        User user = getUser(email);
        ensureFacilityManager(user);

        Venue venue = venueRepository.findByIdAndCreatedBy(venueId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Venue not found or you don't have permission to delete it"));

        deleteImageFromSupabase(venue.getCourtImage());
        venueRepository.delete(venue);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void ensureFacilityManager(User user) {
        if (user.getRole() != Role.FACILITY_MANAGER) {
            throw new IllegalArgumentException("Only facility managers can manage venues");
        }
    }

    private String resolveGameIcon(String sport) {
        return sport.trim().replaceAll("\\s+", "").toLowerCase() + "_icon";
    }

    private String uploadImageToSupabase(MultipartFile imageFile) {
        try {
            if (supabaseUrl == null || supabaseUrl.isEmpty() || supabaseKey == null || supabaseKey.isEmpty()) {
                throw new IllegalArgumentException("Supabase configuration not available");
            }

            String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
            String objectPath = "court-icons/" + fileName;
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + objectPath;

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("Content-Type", "application/octet-stream");

            // Upload file
            byte[] fileBytes = imageFile.getBytes();
            HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

            restTemplate.exchange(uploadUrl, HttpMethod.POST, entity, String.class);

            return objectPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read image file: " + ex.getMessage());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to upload image: " + ex.getMessage());
        }
    }

    private void deleteImageFromSupabase(String imagePath) {
        try {
            if (imagePath == null || imagePath.isBlank() || supabaseUrl == null || supabaseUrl.isEmpty() || supabaseKey == null || supabaseKey.isEmpty()) {
                // Skip deletion if path is empty or Supabase not configured
                return;
            }

            String deleteUrl = supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + imagePath;

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);
        } catch (Exception ex) {
            System.err.println("Failed to delete image from Supabase: " + ex.getMessage());
        }
    }

    private VenueResponse toVenueResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getCourtName(),
                venue.getSport(),
                venue.getCourtLocation(),
                venue.getCourtsAvailable(),
                venue.getPrice(),
                venue.getCourtImage(),
                venue.getGameIcon(),
                venue.getRating());
    }
}
