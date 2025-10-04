package uk.org.kennah.login.model;

public class JwtResponse {
    private final String jwtToken;
    private final String refreshToken;

     public JwtResponse(String jwtToken, String refreshToken) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return this.jwtToken;
    }
    // No setter for token to make it immutable after creation

    public String getRefreshToken() {
        return refreshToken;
    }

    // No setter for refreshToken to make it immutable after creation
}