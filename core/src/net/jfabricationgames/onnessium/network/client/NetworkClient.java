package net.jfabricationgames.onnessium.network.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.network.ConnectException;
import net.jfabricationgames.onnessium.network.network.Network;

@ApplicationScoped
public class NetworkClient {
	
	private int connectionTimeout = 5000;
	
	private Client client;
	private NetworkClientListener listener;
	
	public NetworkClient() {
		client = new Client();
		client.start();
		
		Network.registerDtoClasses(client);
		
		listener = new NetworkClientListener();
		client.addListener(listener);
	}
	
	/**
	 * Connect to a server and return the result as a CompletableFuture, so the result can be handled in the calling instance.
	 * 
	 * NOTE: It is recommended to use <code>.exceptionally(t -> {}).thenAccept(v -> {}).get()</code> on the returned CompletableFuture 
	 * to handle an exception or the successful connection to the server and wait for the execution to finish.
	 */
	public CompletableFuture<Void> connect(String host, int port) {
		if (!isConnected()) {
			return CompletableFuture.runAsync(() -> connectToServer(host, port));
		}
		else {
			Gdx.app.error(getClass().getSimpleName(), "Tried to connect to " + host + ":" + port + " but a connection is already established.");
			return CompletableFuture.runAsync(() -> {});
		}
	}
	
	private void connectToServer(String host, int port) {
		try {
			Gdx.app.log(getClass().getSimpleName(), "Connecting to server at '" + host + "' on port " + port);
			client.connect(connectionTimeout, host, port);
			Gdx.app.log(getClass().getSimpleName(), "Successfully connected to server");
		}
		catch (IOException e) {
			Gdx.app.error(getClass().getSimpleName(), "Connection cound not be established", e);
			throw new ConnectException("Connection could not be established", e);
		}
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}
	
	public void disconnect() {
		client.stop();
	}
	
	/**
	 * Send an object to the server (fire and forget) using TCP.
	 * The objects class must be registered in the NetworkDtoRegistry for the client to be able to serialise and send it.
	 */
	public void send(Object object) {
		client.sendTCP(object);
	}
	
	/**
	 * Send an object to the server using TCP and register a responseHandler that listens for a response of the server.
	 * NOTE: After the response handler received the answer from the server it is automatically unsubscribed from the listener. 
	 */
	public <T> void send(Object object, ClientMessageHandler<T> responseHandler, Class<T> responseType) {
		addMessageHandler(responseType, new SelfRemovingMessageHandler<>(responseHandler, responseType));
		client.sendTCP(object);
	}
	
	public <T> void addMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		listener.addMessageHandler(type, handler);
	}
	
	public <T> void removeMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		listener.removeMessageHandler(type, handler);
	}
	
	public <T> void removeAllMessageHandlersForType(Class<T> type) {
		listener.removeAllMessageHandlersForType(type);
	}
	
	/**
	 * A ClientMessageHandler implementation, that will remove itself from the listener subscription, after receiving the message.
	 * The received message is passed on to the wrapped handler.
	 */
	private class SelfRemovingMessageHandler<T> implements ClientMessageHandler<T> {
		
		private ClientMessageHandler<T> wrappedHandler;
		private Class<T> responseType;
		
		private SelfRemovingMessageHandler(ClientMessageHandler<T> wrappedHandler, Class<T> responseType) {
			this.wrappedHandler = wrappedHandler;
			this.responseType = responseType;
		}
		
		@Override
		public void handleMessage(T message) {
			wrappedHandler.handleMessage(message);
			removeMessageHandler(responseType, this);
		}
	}
}
