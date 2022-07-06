package net.jfabricationgames.onnessium.user.client;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.ClientMessageHandler;
import net.jfabricationgames.onnessium.network.dto.user.UserListDto;
import net.jfabricationgames.onnessium.user.UserListManager;

public class UserListClientHandler implements ClientMessageHandler<UserListDto> {
	
	@Inject
	private UserListManager userListManager;
	
	public UserListClientHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(UserListDto message) {
		userListManager.users = message.users;
		userListManager.onUpdate();
	}
}
