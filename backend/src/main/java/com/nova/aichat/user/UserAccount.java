package com.nova.aichat.user;

/** USERS 테이블에서 조회한 사용자 계정 정보를 담는다. */
public class UserAccount {
    private final Long userId;
    private final String email;
    private final String password;
    private final String name;
    private final String status;

    /** 조회된 사용자 계정의 모든 필드를 초기화한다. */
    public UserAccount(Long userId, String email, String password, String name, String status) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
    }

    /** 사용자 PK를 반환한다. */
    public Long getUserId() { return userId; }
    /** 로그인 이메일을 반환한다. */
    public String getEmail() { return email; }
    /** BCrypt 비밀번호 해시를 반환한다. */
    public String getPassword() { return password; }
    /** 사용자 이름을 반환한다. */
    public String getName() { return name; }
    /** 계정 상태를 반환한다. */
    public String getStatus() { return status; }
}
