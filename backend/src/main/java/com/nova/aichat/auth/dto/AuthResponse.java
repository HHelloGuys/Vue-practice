package com.nova.aichat.auth.dto;

import com.nova.aichat.security.AuthenticatedUser;

/** 로그인 성공 후 Vue에 반환할 JWT와 사용자 정보를 담는다. */
public class AuthResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;
    private final AuthenticatedUser user;

    /** 로그인 응답의 토큰, 만료시간, 사용자 정보를 초기화한다. */
    public AuthResponse(String accessToken, long expiresIn, AuthenticatedUser user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    /** JWT 문자열을 반환한다. */
    public String getAccessToken() { return accessToken; }
    /** Authorization 헤더에서 사용할 토큰 유형을 반환한다. */
    public String getTokenType() { return tokenType; }
    /** 토큰 유효시간을 초 단위로 반환한다. */
    public long getExpiresIn() { return expiresIn; }
    /** 로그인한 사용자 정보를 반환한다. */
    public AuthenticatedUser getUser() { return user; }
}
