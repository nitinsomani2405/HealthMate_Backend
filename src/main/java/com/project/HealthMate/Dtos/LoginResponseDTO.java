package com.project.HealthMate.Dtos;
import jakarta.validation.constraints.NotNull;

public class LoginResponseDTO {

    @NotNull
    private DataDTO data;

    @NotNull
    private String role;
    
    

	public LoginResponseDTO(@NotNull DataDTO data, @NotNull String role) {
		super();
		this.data = data;
		this.role = role;
	}

	public DataDTO getData() {
		return data;
	}

	public void setData(DataDTO data) {
		this.data = data;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    // Constructors, Getters, and Setters
    
}
