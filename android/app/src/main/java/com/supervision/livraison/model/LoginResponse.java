package com.supervision.livraison.model;

/**
 * Login response model matching backend LoginResponse DTO.
 */
public class LoginResponse {
    private String token;
    private String role;
    private Long userId;
    private String userName;
    private String userLogin;

    public String getToken() { return token; }
    public String getRole() { return role; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserLogin() { return userLogin; }
}
