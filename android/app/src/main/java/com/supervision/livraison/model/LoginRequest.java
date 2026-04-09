package com.supervision.livraison.model;

/**
 * Login request model matching backend LoginRequest DTO.
 */
public class LoginRequest {
    private String login;
    private String motP;

    public LoginRequest(String login, String motP) {
        this.login = login;
        this.motP = motP;
    }

    public String getLogin() { return login; }
    public String getMotP() { return motP; }
}
