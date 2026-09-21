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

/** JWT의 발급과 서명 검증을 담당한다. */
@Service
public class JwtTokenService {
    private final Key signingKey;
    private final long expirationSeconds;

    /** Base64 비밀키와 토큰 유효시간을 읽어 서명 키를 구성한다. */
    public JwtTokenService(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationSeconds = expirationSeconds;
    }

    /** 인증된 사용자 정보가 포함된 서명 JWT를 발급한다. */
    public String createToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getUserId().toString())
            .claim("email", user.getEmail())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(signingKey).compact();
    }

    /** JWT 서명과 만료시간을 검증하고 클레임을 반환한다. */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(token).getBody();
    }

    /** 설정된 토큰 유효시간을 초 단위로 반환한다. */
    public long getExpirationSeconds() { return expirationSeconds; }
}
