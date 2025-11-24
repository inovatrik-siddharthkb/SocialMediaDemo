package com.social.media.dto;

public class LoginResponse {
	public String token;
    public String username;
    public String role;
    
    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
