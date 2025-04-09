package com.auth.Dtos;

public class SessionDTO {

    private String accessToken;
    private String refreshToken;
	public SessionDTO(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	// Constructors, Getters, and Setters
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	@Override
	public String toString() {
		return "SessionDTO [accessToken=" + accessToken + ", refreshToken=" + refreshToken + "]";
	}
	

    
}

