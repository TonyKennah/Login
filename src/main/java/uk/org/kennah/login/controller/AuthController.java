package uk.org.kennah.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.org.kennah.login.model.JwtResponse;
import uk.org.kennah.login.model.LoginRequest;
import uk.org.kennah.login.model.RefreshRequest;
import uk.org.kennah.login.security.JwtUtil;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

   
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Simple fake authentication
        if ("user".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String token = JwtUtil.generateToken(request.getUsername());
            String refreshToken = JwtUtil.generateRefreshToken(request.getUsername());
            return ResponseEntity.ok(new JwtResponse(token, refreshToken));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken != null && JwtUtil.validateToken(refreshToken)) {
            String username = JwtUtil.extractUsername(refreshToken);
            String newAccessToken = JwtUtil.generateToken(username);
            String newRefreshToken = JwtUtil.generateRefreshToken(username); // Optionally issue a new refresh token
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
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