package com.nova.aichat.auth.dto;

import com.nova.aichat.security.AuthenticatedUser;

public class AuthResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;
    private final AuthenticatedUser user;

    public AuthResponse(String accessToken, long expiresIn, AuthenticatedUser user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public AuthenticatedUser getUser() { return user; }
}
