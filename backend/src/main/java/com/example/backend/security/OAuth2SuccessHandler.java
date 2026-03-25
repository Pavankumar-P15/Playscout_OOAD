package com.example.backend.security;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            String redirectUrl = frontendUrl + "/complete-profile"
                    + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
            return;
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String redirectUrl = frontendUrl + "/oauth-success"
            + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
            + "&userImage=" + URLEncoder.encode(user.getUserImage() == null ? "avatars/m_avatar1.png" : user.getUserImage(), StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}