package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.exception.AuthException;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        return createPlayerAccount(request, "Email already exists");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(user.getId(), token, user.getRole(), user.getUserImage());
    }

    @Override
    public AuthResponse oauthRegister(RegisterRequest request) {
        return createPlayerAccount(request, "Email already exists");
    }

    private AuthResponse createPlayerAccount(RegisterRequest request, String duplicateMessage) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException(duplicateMessage);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserImage(resolveUserImage(request));
        user.setRole(Role.PLAYER);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(user.getId(), token, user.getRole(), user.getUserImage());
    }

    private String resolveUserImage(RegisterRequest request) {
        String rawValue = request.getUserImage();

        if (rawValue == null || rawValue.isBlank()) {
            return "avatars/m_avatar1.png";
        }

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