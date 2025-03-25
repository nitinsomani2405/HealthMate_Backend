package com.project.HealthMate.Dtos;

import jakarta.validation.constraints.NotNull;

public class RefreshTokenResponseDTO {

    @NotNull
    private SessionDataDTO data;

    public RefreshTokenResponseDTO(SessionDataDTO data) {
        this.data = data;
    }

    public SessionDataDTO getData() {
        return data;
    }

    public void setData(SessionDataDTO data) {
        this.data = data;
    }
}
