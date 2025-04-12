package com.auth.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    private String accessToken;
    private String refreshToken;
	private String accessTokenExpiry;
	private String refreshTokenExpiry;


    
}

