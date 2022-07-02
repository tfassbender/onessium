package net.jfabricationgames.onnessium.network.client;

import java.io.IOException;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.network.Network;

@ApplicationScoped
public class NetworkClient {
	
	private Client client;
	private NetworkClientListener listener;
	
	public NetworkClient() {
		client = new Client();
		client.start();
		
		Network.registerDtoClasses(client);
		
		listener = new NetworkClientListener();
		client.addListener(listener);
	}
	
	public void connect(String host, int port, Runnable onSuccess, Consumer<IOException> onError) {
		if (!isConnected()) {
			new Thread(() -> connectToServer(host, port, onSuccess, onError), "connect").start();
		}
		else {
			Gdx.app.error(getClass().getSimpleName(), "Tried to connect to " + host + ":" + port + " but a connection is already established.");
		}
	}
	
	private void connectToServer(String host, int port, Runnable onSuccess, Consumer<IOException> onError) {
		try {
			Gdx.app.log(getClass().getSimpleName(), "Connecting to server at '" + host + "' on port " + port);
			client.connect(5000, host, port);
			Gdx.app.log(getClass().getSimpleName(), "Successfully connected to server");
			
			if (onSuccess != null) {
				onSuccess.run();
			}
		}
		catch (IOException e) {
			Gdx.app.error(getClass().getSimpleName(), "Connection cound not be established", e);
			
			if (onError != null) {
				onError.accept(e);
			}
		}
	}
	
	public boolean isConnected() {
		return client.isConnected();
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
	}
	
	public <T> void addMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		listener.addMessageHandler(type, handler);
	}
	
	public <T> void removeMessageHandler(Class<T> type, ClientMessageHandler<T> handler) {
		listener.removeMessageHandler(type, handler);
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
