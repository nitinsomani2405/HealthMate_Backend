package com.auth.project.HealthMate.Dtos;

import jakarta.validation.constraints.NotNull;

public class SignupResponseDTO {

    @NotNull
    private UserDataDTO data;

    public SignupResponseDTO(UserDataDTO data) {
        this.data = data;
    }

    public UserDataDTO getData() {
        return data;
    }

    public void setData(UserDataDTO data) {
        this.data = data;
    }
}
