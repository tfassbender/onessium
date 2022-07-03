package net.jfabricationgames.onnessium.network.dto.user;

public class SignUpDto {
	
	public String username;
	public String password;
	
	public boolean successful;
	public String errorMessage;
	
	public SignUpDto setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public SignUpDto setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public SignUpDto setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}
	
	public SignUpDto setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
}
