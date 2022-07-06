package net.jfabricationgames.onnessium.network.dto.user;

/**
 * A login request or answer that is send from the client to the server to try to login, 
 * or that is returned from the server to answer whether the login was successful.
 */
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
