package uk.org.kennah.login.security;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createRefreshTokenCookie(String token, long maxAgeSeconds) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Should be true in production
        cookie.setPath("/auth"); // Restrict cookie to /auth paths
        cookie.setMaxAge((int) maxAgeSeconds);
        return cookie;
    }

    public static Cookie clearRefreshTokenCookie() {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(0); // Expire the cookie immediately
        return cookie;
    }
}