package com.auth.project.HealthMate.Dtos;

import jakarta.validation.constraints.NotNull;

public class RoleResponseDTO {

    @NotNull
    private String role;
    

	public RoleResponseDTO(@NotNull String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    // Constructors, Getters, and Setters
    
}
