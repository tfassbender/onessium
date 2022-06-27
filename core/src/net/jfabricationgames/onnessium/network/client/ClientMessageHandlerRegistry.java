package net.jfabricationgames.onnessium.network.client;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class ClientMessageHandlerRegistry {
	
	private Map<Class<?>, ClientMessageHandler<?>> handlers = new HashMap<>();
	
	public <T> void registerHandler(Class<T> type, ClientMessageHandler<T> handler) {
		handlers.put(type, handler);
	}
	
	public void handleMessage(Connection connection, Object message) {
		handleMessage(connection, message, message.getClass());
	}
	
	private <T> void handleMessage(Connection connection, Object message, Class<T> messageType) {
		handlerForType(messageType).handleMessage(connection, messageType.cast(message));
	}
	
	@SuppressWarnings("unchecked")
	private <T> ClientMessageHandler<T> handlerForType(Class<T> type) {
		if (!handlers.containsKey(type)) {
			throw new IllegalStateException("No handler is known for the type " + type.getName());
		}
		
		return (ClientMessageHandler<T>) handlers.get(type);
	}
}
