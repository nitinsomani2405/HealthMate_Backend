package com.auth.Dtos;

import jakarta.validation.constraints.NotNull;

public class SessionDataDTO {

    @NotNull
    private SessionDTO session;

    public SessionDataDTO(SessionDTO session) {
        this.session = session;
    }

    public SessionDTO getSession() {
        return session;
    }

    public void setSession(SessionDTO session) {
        this.session = session;
    }
}
