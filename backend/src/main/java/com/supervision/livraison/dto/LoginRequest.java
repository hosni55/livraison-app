package com.supervision.livraison.dto;

/**
 * Login request DTO.
 */
public class LoginRequest {
    private String login;
    private String motP;

    public LoginRequest() {}

    public LoginRequest(String login, String motP) {
        this.login = login;
        this.motP = motP;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotP() { return motP; }
    public void setMotP(String motP) { this.motP = motP; }
}
