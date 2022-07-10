package net.jfabricationgames.onnessium.chat;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.client.ClientMessageHandler;
import net.jfabricationgames.onnessium.network.dto.chat.ChatMessageDto;

public class ChatMessageClientHandler implements ClientMessageHandler<ChatMessageDto> {
	
	@Inject
	private ChatClientManager chatManager;
	
	public ChatMessageClientHandler() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void handleMessage(ChatMessageDto message) {
		chatManager.receivedChatMessage(message);
	}
}
