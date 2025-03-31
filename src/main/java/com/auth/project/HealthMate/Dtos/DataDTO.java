package com.auth.project.HealthMate.Dtos;
import jakarta.validation.constraints.NotNull;

public class DataDTO {

    @NotNull
    private UserDTO user;

    @NotNull
    private SessionDTO session;
    

	public DataDTO(@NotNull UserDTO user, @NotNull SessionDTO session) {
		super();
		this.user = user;
		this.session = session;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public SessionDTO getSession() {
		return session;
	}

	public void setSession(SessionDTO session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "DataDTO [user=" + user + ", session=" + session + "]";
	}

    // Constructors, Getters, and Setters
	
    
}
