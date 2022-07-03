package net.jfabricationgames.onnessium.network.server.handler;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.server.NetworkConnection;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandler;
import net.jfabricationgames.onnessium.network.server.user.UserAccount;
import net.jfabricationgames.onnessium.network.server.user.UserManager;
import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;
import net.jfabricationgames.onnessium.user.dto.SignUpDto;

public class SignUpServerHandler implements ServerMessageHandler<SignUpDto> {
	
	@Inject
	private UserManager userManager;
	
	public SignUpServerHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(NetworkConnection connection, SignUpDto message) {
		if (userManager.isUserRegistered(message.username)) {
			message.successful = false;
			message.errorMessage = "A user with the name \"" + message.username + "\" is already registered on this server.";
		}
		else {
			userManager.addUser(new UserAccount()//
					.setUsername(message.username)//
					.setEncryptedPassword(PasswordEncryptor.encrypt(message.password))//
					.setOnline(true));
			
			message.successful = true;
		}
		
		// send the response to the client
		connection.sendTCP(message);
	}
}
