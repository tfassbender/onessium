package net.jfabricationgames.onnessium;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.chat.ChatMessageClientHandler;
import net.jfabricationgames.onnessium.chat.dto.ChatMessageDto;
import net.jfabricationgames.onnessium.network.client.Client;
import net.jfabricationgames.onnessium.network.dto.user.UserListDto;
import net.jfabricationgames.onnessium.user.client.UserListClientHandler;

/**
 * Registers all global client handlers.
 */
public class ClientHandlerRegistry {
	
	@Inject
	private Client client;
	
	/**
	 * Register all DTOs in the Network class, to be able to serialise them and send them to the server / client.
	 */
	public static void initializeClientHandlers() {
		new ClientHandlerRegistry().initialize();
	}
	
	public ClientHandlerRegistry() {
		CdiContainer.injectTo(this);
	}
	
	private void initialize() {
		client.addMessageHandler(UserListDto.class, new UserListClientHandler());
		client.addMessageHandler(ChatMessageDto.class, new ChatMessageClientHandler());
	}
}
