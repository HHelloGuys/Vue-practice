package com.nova.aichat.security;

/** 인증이 완료된 사용자의 최소 식별 정보를 표현한다. */
public class AuthenticatedUser {
    private final Long userId;
    private final String email;
    private final String name;

    /** 인증 사용자 식별 정보를 초기화한다. */
    public AuthenticatedUser(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    /** 사용자 PK를 반환한다. */
    public Long getUserId() { return userId; }
    /** 사용자 이메일을 반환한다. */
    public String getEmail() { return email; }
    /** 사용자 이름을 반환한다. */
    public String getName() { return name; }
}
