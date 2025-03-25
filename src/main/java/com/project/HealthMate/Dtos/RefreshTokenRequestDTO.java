package com.project.HealthMate.Dtos;

public class RefreshTokenRequestDTO {
	
	private String refreshToken;

	public RefreshTokenRequestDTO(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
}
