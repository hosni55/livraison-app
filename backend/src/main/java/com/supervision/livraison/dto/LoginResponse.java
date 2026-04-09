package com.supervision.livraison.dto;

/**
 * Login response DTO — returns JWT token and user info.
 */
public class LoginResponse {
    private String token;
    private String role;
    private Long userId;
    private String userName;
    private String userLogin;

    public LoginResponse() {}

    public LoginResponse(String token, String role, Long userId, String userName, String userLogin) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.userName = userName;
        this.userLogin = userLogin;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserLogin() { return userLogin; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }
}
