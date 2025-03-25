package com.project.HealthMate.Dtos;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String email;
    private Instant createdAt;
    // Constructors, Getters, and Setters
	public UserDTO(UUID id, String email, Instant createdAt) {
		super();
		this.id = id;
		this.email = email;
		this.createdAt = createdAt;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public	Instant getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", email=" + email + ", createdAt=" + createdAt + "]";
	}
	

	
    
}
