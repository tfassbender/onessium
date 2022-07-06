package net.jfabricationgames.onnessium.network.dto.user;

/**
 * A sign up request or answer that is send from the client to the server to try to sign up, 
 * or that is returned from the server to answer whether the sign up was successful.
 */
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
