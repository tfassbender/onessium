package net.jfabricationgames.onnessium.user.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.Client;
import net.jfabricationgames.onnessium.network.dto.user.LoginDto;
import net.jfabricationgames.onnessium.network.dto.user.SignUpDto;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.user.UserListManager;
import net.jfabricationgames.onnessium.util.Wrapper;

public class LoginHandler {
	
	private static final String DEFAULT_LOGIN_FAILED_MESSAGE_CANNOT_CONNECT = "Login failed - Cannot connect to server";
	private static final String DEFAULT_LOGIN_FAILED_MESSAGE_SERVER_NOT_RESPONDING = "Login failed - The server is not responding";
	private static final String DEFAULT_SIGNUP_FAILED_MESSAGE_CANNOT_CONNECT = "Sign up failed - Cannot connect to server";
	private static final String DEFAULT_SIGNUP_FAILED_MESSAGE_SERVER_NOT_RESPONDING = "Sign up failed - The server is not responding";
	
	@Inject
	private Client client;
	@Inject
	private UserListManager userListManager;
	
	private LoginException loginException;
	private LoginException signUpException;
	
	private long responseWaitingTimeInMilliseconds = 5000;
	
	public LoginHandler() {
		CdiContainer.injectTo(this);
	}
	
	public void login(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		Wrapper<CompletableFuture<Void>> loginResponseFuture = Wrapper.empty();
		
		loginException = null;
		
		try {
			client.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE_CANNOT_CONNECT, t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a login request
						loginResponseFuture.wrapped = client.send(new LoginDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								loginException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, LoginDto.class, responseWaitingTimeInMilliseconds);
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE_CANNOT_CONNECT, e);
		}
		
		if (loginException == null) {
			try {
				// wait for the response of the server (for a maximum of usually 5 seconds, before the login is assumed to be not successful)
				loginResponseFuture.wrapped.get();
			}
			catch (InterruptedException | ExecutionException e) {
				loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE_SERVER_NOT_RESPONDING, e);
			}
		}
		
		if (loginException != null) {
			throw loginException;
		}
		
		userListManager.localUser = new UserDto().setUsername(username).setOnline(true);
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public void signUp(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		Wrapper<CompletableFuture<Void>> signupResponseFuture = Wrapper.empty();
		
		signUpException = null;
		
		try {
			client.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						signUpException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE_CANNOT_CONNECT, t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a sign up request
						signupResponseFuture.wrapped = client.send(new SignUpDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								signUpException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, SignUpDto.class, responseWaitingTimeInMilliseconds);
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			signUpException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE_CANNOT_CONNECT, e);
		}
		
		if (signUpException == null) {
			try {
				// wait for the response of the server (for a maximum of usually 5 seconds, before the sign up is assumed to be not successful)
				signupResponseFuture.wrapped.get();
			}
			catch (InterruptedException | ExecutionException e) {
				signUpException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE_SERVER_NOT_RESPONDING, e);
			}
		}
		
		if (signUpException != null) {
			throw signUpException;
		}
		
		userListManager.localUser = new UserDto().setUsername(username).setOnline(true);
		
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
