package net.jfabricationgames.onnessium.network.server.handler;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.dto.user.LogoutDto;
import net.jfabricationgames.onnessium.network.server.Connection;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandler;
import net.jfabricationgames.onnessium.network.server.user.UserManager;

public class LogoutServerHandler implements ServerMessageHandler<LogoutDto> {
	
	@Inject
	private UserManager userManager;
	
	public LogoutServerHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(Connection connection, LogoutDto message) {
		userManager.setOnlineStateOf(null, false, connection);
	}
}
