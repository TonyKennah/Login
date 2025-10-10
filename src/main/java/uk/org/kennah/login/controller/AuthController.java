package uk.org.kennah.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import uk.co.pluckier.model.User;
import uk.co.pluckier.mongo.UserRepo;
import uk.co.pluckier.mongo.Repo;
import uk.org.kennah.login.model.JwtResponse;
import uk.org.kennah.login.model.LoginRequest;
import uk.org.kennah.login.security.CookieUtil;
import uk.org.kennah.login.security.JwtUtil;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Value("${register.url:register}") // Default to "register" if not set
    private String registerUrl;

    @Value("${forgotten.url:forgotten}") // Default to "forgotten" if not set
    private String forgottenPasswordUrl;

    @Value("${logo.url:Login}") // Default to "Login" if not set
    private String logoUrl;

    @Value("${app.url:app.html}") // Default to "app.html" if not set
    private String appUrl;

    @Value("${logout.redirect.url:index.html}") // Default to "index.html" if not set
    private String logoutRedirectUrl;

    @Value("${favicon.url:/favicon.ico}") // Default to local favicon if not set
    private String faviconUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            // Delegate authentication to the AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // If we get here, authentication was successful
            String token = JwtUtil.generateToken(request.getUsername());
            String refreshToken = JwtUtil.generateRefreshToken(request.getUsername());
            response.addCookie(CookieUtil.createRefreshTokenCookie(refreshToken, JwtUtil.REFRESH_EXPIRATION / 1000));
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) { // Catches BadCredentialsException, etc.
            return ResponseEntity.status(401).body("Invalid credentials");
        }
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
                "logoUrl", logoUrl,
                "appUrl", appUrl,
                "logoutRedirectUrl", logoutRedirectUrl,
                "faviconUrl", faviconUrl
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