package uk.org.kennah.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.org.kennah.login.model.JwtResponse;
import uk.org.kennah.login.model.LoginRequest;
import uk.org.kennah.login.security.CookieUtil;
import uk.org.kennah.login.security.JwtUtil;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${register.url:register}") // Default to "register" if not set
    private String registerUrl;

    @Value("${forgotten.url:forgotten}") // Default to "forgotten" if not set
    private String forgottenPasswordUrl;

    @Value("${logo.url:Login}") // Default to "Login" if not set
    private String logoUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // Simple fake authentication
        if ("user".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String token = JwtUtil.generateToken(request.getUsername());
            String refreshToken = JwtUtil.generateRefreshToken(request.getUsername());

            response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken, JwtUtil.REFRESH_EXPIRATION / 1000));

            return ResponseEntity.ok(new JwtResponse(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && JwtUtil.validateToken(refreshToken)) {
            String username = JwtUtil.extractUsername(refreshToken);
            String newAccessToken = JwtUtil.generateToken(username);
            String newRefreshToken = JwtUtil.generateRefreshToken(username);

            response.addCookie(CookieUtil.createRefreshTokenCookie(newRefreshToken, JwtUtil.REFRESH_EXPIRATION / 1000));

            return ResponseEntity.ok(new JwtResponse(newAccessToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(CookieUtil.clearRefreshTokenCookie());
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(Map.of(
                "registerUrl", registerUrl,
                "forgottenPasswordUrl", forgottenPasswordUrl,
                "logoUrl", logoUrl
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication, HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String responseText = "Hello, " + authentication.getName() + "! ";

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Date expiration = JwtUtil.extractExpiration(token);
                responseText += "Your token expires at: " + expiration;
            } catch (Exception e) {
                // Token might be invalid, but we proceed with the basic message
            }
        }
        return ResponseEntity.ok(responseText);
    }
}