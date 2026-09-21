package com.nova.aichat.auth.dto;

/** 로그인 화면에서 전달된 이메일과 비밀번호를 받는다. */
public class LoginRequest {
    private String email;
    private String password;

    /** 입력된 이메일을 반환한다. */
    public String getEmail() { return email; }
    /** 입력된 이메일을 설정한다. */
    public void setEmail(String email) { this.email = email; }
    /** 입력된 비밀번호를 반환한다. */
    public String getPassword() { return password; }
    /** 입력된 비밀번호를 설정한다. */
    public void setPassword(String password) { this.password = password; }
}
