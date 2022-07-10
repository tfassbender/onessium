package net.jfabricationgames.onnessium.chat;

import java.util.ArrayList;
import java.util.List;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.chat.dto.ChatMessageDto;
import net.jfabricationgames.onnessium.network.client.Client;

@ApplicationScoped
public class ChatClientManager {
	
	@Inject
	private Client client;
	
	private List<ChatMessageListener> chatMessageListeners = new ArrayList<>();
	
	public ChatClientManager() {
		CdiContainer.injectTo(this);
	}
	
	protected void receivedChatMessage(ChatMessageDto message) {
		chatMessageListeners.forEach(listener -> listener.receiveChatMessage(message));
	}
	
	public void sendChatMessage(ChatMessageDto message) {
		client.send(message);
	}
	
	public void addChatMessageListener(ChatMessageListener listener) {
		chatMessageListeners.add(listener);
	}
	
	public void removeChatMessageListener(ChatMessageListener listener) {
		chatMessageListeners.remove(listener);
	}
	
	public static interface ChatMessageListener {
		
		public void receiveChatMessage(ChatMessageDto message);
	}
}
