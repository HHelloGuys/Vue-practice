package com.nova.aichat.user;

public class UserAccount {
    private final Long userId;
    private final String email;
    private final String password;
    private final String name;
    private final String status;

    public UserAccount(Long userId, String email, String password, String name, String status) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getStatus() { return status; }
}
