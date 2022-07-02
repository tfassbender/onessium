package net.jfabricationgames.onnessium.user.client;

import java.util.concurrent.ExecutionException;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.NetworkClient;
import net.jfabricationgames.onnessium.user.dto.LoginDto;
import net.jfabricationgames.onnessium.user.dto.SignUpDto;

public class LoginHandler {
	
	@Inject
	private NetworkClient networkClient;
	
	private LoginException loginException;
	
	public LoginHandler() {
		CdiContainer.injectTo(this);
	}
	
	public void login(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		try {
			networkClient.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						loginException = new LoginException("Login failed - Cannot connect to server", t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a login request
						networkClient.send(new LoginDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								loginException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, LoginDto.class);
						//TODO wait for the response to the sent DTO
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			loginException = new LoginException("Login failed - Cannot connect to server", e);
		}
		
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public void signUp(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		try {
			networkClient.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						loginException = new LoginException("Sign up failed - Cannot connect to server", t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a sign up request
						networkClient.send(new SignUpDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								loginException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, SignUpDto.class);
						//TODO wait for the response to the sent DTO
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			loginException = new LoginException("Sign up failed - Cannot connect to server", e);
		}
		
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public class LoginException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		public LoginException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public LoginException(String message) {
			super(message);
		}
	}
}
