package uk.org.kennah.login.model;

public class JwtResponse {
    private final String token;

     public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
    // No setter for token to make it immutable after creation
}