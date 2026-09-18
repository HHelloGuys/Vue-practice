package com.nova.aichat.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final Key signingKey;
    private final long expirationSeconds;

    public JwtTokenService(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getUserId().toString())
            .claim("email", user.getEmail())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(signingKey).compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token).getBody();
    }

    public long getExpirationSeconds() { return expirationSeconds; }
}
