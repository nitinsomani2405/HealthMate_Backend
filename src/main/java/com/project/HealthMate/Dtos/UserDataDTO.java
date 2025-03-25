package com.project.HealthMate.Dtos;

import jakarta.validation.constraints.NotNull;

public class UserDataDTO {

    @NotNull
    private UserDTO user;

    public UserDataDTO(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
