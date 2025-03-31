package com.auth.project.HealthMate.Dtos;

public class ErrorResponseDTO {

    private String error;
    private String message;

    public ErrorResponseDTO(String error, String message) {
        this.error = error;
        this.message = message;
    }
    
 
	public ErrorResponseDTO(String error) {
		super();
		this.error = error;
	}


	public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
