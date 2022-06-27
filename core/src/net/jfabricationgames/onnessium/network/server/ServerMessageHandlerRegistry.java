package net.jfabricationgames.onnessium.network.server;

import java.util.HashMap;
import java.util.Map;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class ServerMessageHandlerRegistry {
	
	private Map<Class<?>, ServerMessageHandler<?>> handlers = new HashMap<>();
	
	public <T> void registerHandler(Class<T> type, ServerMessageHandler<T> handler) {
		handlers.put(type, handler);
	}
	
	public void handleMessage(NetworkConnection connection, Object message) {
		handleMessage(connection, message, message.getClass());
	}
	
	private <T> void handleMessage(NetworkConnection connection, Object message, Class<T> messageType) {
		handlerForType(messageType).handleMessage(connection, messageType.cast(message));
	}
	
	@SuppressWarnings("unchecked")
	private <T> ServerMessageHandler<T> handlerForType(Class<T> type) {
		if (!handlers.containsKey(type)) {
			throw new IllegalStateException("No handler is known for the type " + type.getName());
		}
		
		return (ServerMessageHandler<T>) handlers.get(type);
	}
}
