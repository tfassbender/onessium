package net.jfabricationgames.onnessium.network.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.jfabricationgames.cdi.CdiContainer;

public class NetworkClientListener extends Listener {
	
	private Map<Class<?>, List<ClientMessageHandler<?>>> messageHandlers;
	
	public NetworkClientListener() {
		CdiContainer.injectTo(this);
	}
	
	@Override
	public void connected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "Connection established");
	}
	
	@Override
	public void received(Connection connection, Object object) {
		getHandlersForType(object.getClass()).forEach(handler -> handleMessage(handler, object, object.getClass()));
	}
	
	@SuppressWarnings("unchecked")
	private <T> void handleMessage(ClientMessageHandler<?> handler, Object message, Class<T> messageType) {
		ClientMessageHandler<T> typedHandler = (ClientMessageHandler<T>) handler;
		typedHandler.handleMessage(messageType.cast(message));
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<ClientMessageHandler<T>> getHandlersForType(Class<T> type) {
		if (!messageHandlers.containsKey(type)) {
			Collections.emptyList();
		}
		
		return messageHandlers.computeIfAbsent(type, t -> new ArrayList<>()).stream() //
				.map(handler -> (ClientMessageHandler<T>) handler) //
				.collect(Collectors.toList());
	}
	
	@Override
	public void disconnected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "Disconnected");
	}
	
	protected <T> void addMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		messageHandlers.computeIfAbsent(type, t -> new ArrayList<>()).add(handler);
	}
	
	protected <T> void removeMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		messageHandlers.computeIfAbsent(type, t -> new ArrayList<>()).remove(handler);
	}
}
