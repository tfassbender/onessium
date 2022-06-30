package net.jfabricationgames.onnessium.user.client;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.NetworkClient;

public class LoginHandler {
	
	@Inject
	private NetworkClient networkClient;
	
	private LoginException loginException;
	
	public LoginHandler() {
		CdiContainer.injectTo(this);
	}
	
	public void login(String username, String password, String host, int port) throws LoginException {
		networkClient.connect(host, port, () -> {
			// TODO login 
			// TODO handle login errors
		}, ioException -> {
			loginException = new LoginException("Login failed - Cannot connect to server", ioException);
		});
		
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public void signUp(String username, String password, String host, int port) throws LoginException {
		networkClient.connect(host, port, () -> {
			// TODO signUp
			// TODO handle sign up errors
		}, ioException -> {
			loginException = new LoginException("Sign up failed - Cannot connect to server", ioException);
		});
		
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
