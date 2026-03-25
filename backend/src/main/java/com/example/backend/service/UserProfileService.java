package com.example.backend.service;

import com.example.backend.dto.UpdateProfileRequest;
import com.example.backend.dto.UserProfileResponse;
import com.example.backend.exception.AuthException;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserProfileService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        return toProfileResponse(user, null);
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim();
            if (!normalizedEmail.equalsIgnoreCase(user.getEmail())
                    && userRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new AuthException("Email already exists");
            }
            user.setEmail(normalizedEmail);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) {
                throw new AuthException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getUserImage() != null && !request.getUserImage().isBlank()) {
            user.setUserImage(resolveUserImage(request.getUserImage()));
        }

        userRepository.save(user);

        String refreshedToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return toProfileResponse(user, refreshedToken);
    }

    private UserProfileResponse toProfileResponse(User user, String token) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getUserImage(),
                token);
    }

    private String resolveUserImage(String rawValue) {
        String normalized = rawValue.trim();

        if (normalized.startsWith("http://") || normalized.startsWith("https://") || normalized.startsWith("avatars/")) {
            return normalized;
        }

        if (normalized.endsWith(".png")) {
            return "avatars/" + normalized;
        }

        return "avatars/" + normalized + ".png";
    }
}