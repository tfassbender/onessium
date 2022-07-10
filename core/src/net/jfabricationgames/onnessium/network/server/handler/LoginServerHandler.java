package net.jfabricationgames.onnessium.network.server.handler;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.dto.user.LoginDto;
import net.jfabricationgames.onnessium.network.server.Connection;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandler;
import net.jfabricationgames.onnessium.network.server.user.UserManager;

public class LoginServerHandler implements ServerMessageHandler<LoginDto> {
	
	@Inject
	private UserManager userManager;
	
	public LoginServerHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(Connection connection, LoginDto message) {
		if (!userManager.isUserRegistered(message.username)) {
			message.successful = false;
			message.errorMessage = "A user with the name \"" + message.username + "\" is not registered on this server.";
		}
		else if (!userManager.isLoginCorrect(message.username, message.password)) {
			message.successful = false;
			message.errorMessage = "The password for user \"" + message.username + "\" is not correct.";
		}
		else {
			message.successful = true;
			userManager.setOnlineStateOf(message.username, true, connection);
		}
		
		// send the response to the client
		connection.sendTCP(message);
	}
}
