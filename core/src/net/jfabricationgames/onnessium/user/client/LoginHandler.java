package net.jfabricationgames.onnessium.user.client;

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
	
	public void login(String username, String password, String host, int port) throws LoginException {
		networkClient.connect(host, port, () -> {
			networkClient.send(new LoginDto().setUsername(username).setPassword(password), response -> {
				if (!response.successful) {
					loginException = new LoginException(response.errorMessage);
				}
				else {
					//TODO login successful
				}
			}, LoginDto.class);
		}, ioException -> {
			loginException = new LoginException("Login failed - Cannot connect to server", ioException);
		});
		
		//TODO this won't wait for the response
		if (loginException != null) {
			throw loginException;
		}
		
		LastUsedClientSettings.store(username, password, host, port);
	}
	
	public void signUp(String username, String password, String host, int port) throws LoginException {
		networkClient.connect(host, port, () -> {
			networkClient.send(new SignUpDto().setUsername(username).setPassword(password), response -> {
				if (!response.successful) {
					loginException = new LoginException(response.errorMessage);
				}
				else {
					//TODO sign up successful
				}
			}, SignUpDto.class);
			//TODO handle server response
		}, ioException -> {
			loginException = new LoginException("Sign up failed - Cannot connect to server", ioException);
		});
		
		//TODO this won't wait for the response
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
