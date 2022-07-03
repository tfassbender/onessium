package net.jfabricationgames.onnessium.network.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class ServerMessageHandlerRegistry {
	
	private static final Logger log = LoggerFactory.getLogger(ServerMessageHandlerRegistry.class);
	
	private Map<Class<?>, ServerMessageHandler<?>> handlers = new HashMap<>();
	
	public <T> void addHandler(Class<T> type, ServerMessageHandler<T> handler) {
		handlers.put(type, handler);
	}
	
	public <T> void removeHandler(Class<T> type) {
		handlers.remove(type);
	}
	
	public void removeAllHandlers() {
		handlers.clear();
	}
	
	public void handleMessage(NetworkConnection connection, Object message) {
		handleMessage(connection, message, message.getClass());
	}
	
	private <T> void handleMessage(NetworkConnection connection, Object message, Class<T> messageType) {
		handlerForType(messageType).ifPresent(handler -> handler.handleMessage(connection, messageType.cast(message)));
	}
	
	@SuppressWarnings("unchecked")
	private <T> Optional<ServerMessageHandler<T>> handlerForType(Class<T> type) {
		if (!handlers.containsKey(type)) {
			log.error("No handler is known for the type " + type.getName());
			return Optional.empty();
		}
		
		return Optional.of((ServerMessageHandler<T>) handlers.get(type));
	}
}
