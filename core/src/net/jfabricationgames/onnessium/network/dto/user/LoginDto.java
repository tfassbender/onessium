package net.jfabricationgames.onnessium.network.dto.user;

public class LoginDto {
	
	public String username;
	public String password;
	
	public boolean successful;
	public String errorMessage;
	
	public LoginDto setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public LoginDto setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public LoginDto setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}
	
	public LoginDto setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
}
