package com.example.backend.service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.exception.AuthException;
import com.example.backend.enums.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.bootstrap.email:}")
    private String adminEmail;

    @Value("${admin.bootstrap.password:}")
    private String adminPassword;

    @Value("${admin.bootstrap.name:Admin}")
    private String adminName;

    @Value("${admin.bootstrap.image:avatars/m_avatar1.png}")
    private String adminImage;

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
        return createAccount(request, "Email already exists", resolveRequestedRole(request));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> tryBootstrapAdmin(request));

        if (user == null) {
            throw new AuthException("User not found");
        }

        if (Boolean.TRUE.equals(user.getSuspended())) {
            throw new AuthException("Your account is suspended.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid password");
        }

        if (isBootstrapAdminEmail(user.getEmail()) && user.getRole() != Role.ADMIN) {
            user.setRole(Role.ADMIN);
            user.setSuspended(false);
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(user.getId(), token, user.getRole(), user.getUserImage());
    }

    @Override
    public AuthResponse oauthRegister(RegisterRequest request) {
        return createAccount(request, "Email already exists", resolveRequestedRole(request));
    }

    private AuthResponse createAccount(RegisterRequest request, String duplicateMessage, Role role) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException(duplicateMessage);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserImage(resolveUserImage(request));
        user.setRole(role);
        user.setSuspended(false);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(user.getId(), token, user.getRole(), user.getUserImage());
    }

    private User tryBootstrapAdmin(LoginRequest request) {
        if (!isBootstrapAdminEmail(request.getEmail())) {
            return null;
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            return null;
        }
        if (!adminPassword.equals(request.getPassword())) {
            return null;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setUserImage(adminImage);
        admin.setRole(Role.ADMIN);
        admin.setSuspended(false);
        return userRepository.save(admin);
    }

    private boolean isBootstrapAdminEmail(String email) {
        return adminEmail != null
            && !adminEmail.isBlank()
            && email != null
            && adminEmail.equalsIgnoreCase(email.trim());
    }

    private Role resolveRequestedRole(RegisterRequest request) {
        String rawRole = request.getRole();
        if (rawRole == null || rawRole.isBlank()) {
            return Role.PLAYER;
        }

        try {
            Role role = Role.valueOf(rawRole.trim().toUpperCase());
            if (role == Role.ADMIN) {
                throw new AuthException("Invalid role selected");
            }
            return role;
        } catch (IllegalArgumentException ex) {
            throw new AuthException("Invalid role selected");
        }
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
