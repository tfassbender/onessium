package net.jfabricationgames.onnessium.user.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.NetworkClient;
import net.jfabricationgames.onnessium.user.dto.LoginDto;
import net.jfabricationgames.onnessium.user.dto.SignUpDto;
import net.jfabricationgames.onnessium.util.Wrapper;

public class LoginHandler {
	
	private static final String DEFAULT_LOGIN_FAILED_MESSAGE = "Login failed - Cannot connect to server";
	private static final String DEFAULT_SIGNUP_FAILED_MESSAGE = "Sign up failed - Cannot connect to server";
	
	@Inject
	private NetworkClient networkClient;
	
	private LoginException loginException;
	
	private long responseWaitingTimeInMilliseconds = 5000;
	
	public LoginHandler() {
		CdiContainer.injectTo(this);
	}
	
	public void login(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		Wrapper<CompletableFuture<Void>> loginResponseFuture = Wrapper.empty();
		
		try {
			networkClient.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE, t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a login request
						loginResponseFuture.wrapped = networkClient.send(new LoginDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								loginException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, LoginDto.class);
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE, e);
		}
		
		try {
			// wait for the response of the server (for a maximum of usually 5 seconds, before the login is assumed to be not successful)
			loginResponseFuture.wrapped.get(responseWaitingTimeInMilliseconds, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			loginException = new LoginException(DEFAULT_LOGIN_FAILED_MESSAGE, e);
		}
		
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public void signUp(String username, String password, String host, int port, Runnable onComplete) throws LoginException {
		Wrapper<CompletableFuture<Void>> signupResponseFuture = Wrapper.empty();
		
		try {
			networkClient.connect(host, port) //
					.exceptionally(t -> {
						// the connection could not be established
						loginException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE, t);
						return null;
					}) //
					.thenAccept(v -> {
						// the connection was successfully established, so send a sign up request
						signupResponseFuture.wrapped = networkClient.send(new SignUpDto().setUsername(username).setPassword(password), response -> {
							if (!response.successful) {
								loginException = new LoginException(response.errorMessage);
							}
							else {
								onComplete.run();
							}
						}, SignUpDto.class);
					}) //
					.get();
		}
		catch (InterruptedException | ExecutionException e) {
			loginException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE, e);
		}
		
		try {
			// wait for the response of the server (for a maximum of usually 5 seconds, before the sign up is assumed to be not successful)
			signupResponseFuture.wrapped.get(responseWaitingTimeInMilliseconds, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			loginException = new LoginException(DEFAULT_SIGNUP_FAILED_MESSAGE, e);
		}
		
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	/**
	 * Use in tests to not wait to long for the response.
	 */
	protected void setResponseWaitingTimeInMilliseconds(long responseWaitingTimeInMilliseconds) {
		this.responseWaitingTimeInMilliseconds = responseWaitingTimeInMilliseconds;
	}
	
	/**
	 * Use after tests to reset the response waiting time.
	 */
	protected void resetResponseWaitingTimeInMilliseconds() {
		this.responseWaitingTimeInMilliseconds = 5000;
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
