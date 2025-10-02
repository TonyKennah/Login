package uk.org.kennah.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uk.org.kennah.login.model.JwtResponse;
import uk.org.kennah.login.model.LoginRequest;
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
            return ResponseEntity.ok(new JwtResponse(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
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