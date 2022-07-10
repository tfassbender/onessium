package net.jfabricationgames.onnessium.network.server.handler;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.chat.dto.ChatMessageDto;
import net.jfabricationgames.onnessium.network.server.Connection;
import net.jfabricationgames.onnessium.network.server.Server;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandler;

public class ChatMessageServerHandler implements ServerMessageHandler<ChatMessageDto> {
	
	@Inject
	private Server server;
	
	public ChatMessageServerHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(Connection connection, ChatMessageDto message) {
		server.sendToAllBut(connection, message);
	}
}
