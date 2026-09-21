package com.nova.aichat.auth.dto;

/** 회원가입 화면에서 전달된 사용자 정보를 받는다. */
public class RegisterRequest {
    private String email;
    private String password;
    private String name;

    /** 가입 이메일을 반환한다. */
    public String getEmail() { return email; }
    /** 가입 이메일을 설정한다. */
    public void setEmail(String email) { this.email = email; }
    /** 가입 비밀번호를 반환한다. */
    public String getPassword() { return password; }
    /** 가입 비밀번호를 설정한다. */
    public void setPassword(String password) { this.password = password; }
    /** 사용자 이름을 반환한다. */
    public String getName() { return name; }
    /** 사용자 이름을 설정한다. */
    public void setName(String name) { this.name = name; }
}
