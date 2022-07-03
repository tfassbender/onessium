package net.jfabricationgames.onnessium.network.client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.network.shared.exception.ConnectException;
import net.jfabricationgames.onnessium.network.shared.exception.ResponseNotReceivedException;

@ApplicationScoped
public class NetworkClient {
	
	public static final int DIRECT_RESPONSE_MAXIMUM_WAITING_TIME_IN_MILLISECONDS = 5000;
	
	private int connectionTimeoutInMilliseconds = 5000; // can be changed in tests (via reflection)
	
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
			client.connect(connectionTimeoutInMilliseconds, host, port);
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
	 * This method will then return a CompletableFuture, that is notified, when the expected response from the server 
	 * was handled in the responseHandler parameter.
	 * 
	 * NOTE: After the response handler received the answer from the server it is automatically unsubscribed from the listener.
	 * 
	 * NOTE: The typical usage of this method is:
	 * <code>
	 * send(object, responseHandler, responseType) //
	 *     .exceptionally(throwable -> { // the exceptionally part is optional
	 *       // handle the exception
	 *       return null; // return type must be Void
	 *     })
	 *     .get(); // wait for the response of the server (for a maximum of 5 seconds)
	 * </code>
	 * 
	 * NOTE: To use a custom maximum waiting time use the method {@link NetworkClient#send(Object, ClientMessageHandler, Class, long)}.
	 */
	public <T> CompletableFuture<Void> send(Object object, ClientMessageHandler<T> responseHandler, Class<T> responseType) {
		return send(object, responseHandler, responseType, DIRECT_RESPONSE_MAXIMUM_WAITING_TIME_IN_MILLISECONDS);
	}
	
	/**
	 * See {@link NetworkClient#send(Object, ClientMessageHandler, Class)}
	 */
	public <T> CompletableFuture<Void> send(Object object, ClientMessageHandler<T> responseHandler, Class<T> responseType, long maximumWaitingTimeInMilliseconds) {
		CompletableFuture<Void> waitingFuture = createWaitingCompletableFuture(maximumWaitingTimeInMilliseconds);
		addMessageHandler(responseType, new SelfRemovingMessageHandler<>(responseHandler, responseType, waitingFuture));
		client.sendTCP(object);
		
		return waitingFuture;
	}
	
	/**
	 * Create a CompletableFuture that just waits, till the CompeltableFuture.complete method is called on it, or till the 
	 * configured time amount for an answer is over.
	 */
	private CompletableFuture<Void> createWaitingCompletableFuture(long maximumWaitingTimeInMilliseconds) {
		return CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(maximumWaitingTimeInMilliseconds);
			}
			catch (InterruptedException e) {
				// should never happen
				throw new ResponseNotReceivedException("The waiting for the response was interrupted. The maximum waiting time was " + //
						maximumWaitingTimeInMilliseconds + " ms", e);
			}
			
			/*
			 * if the response was received in the meantime, the CompletableFuture would have already been handled, 
			 * so we just throw the exception here, to end the CompletableFuture exceptionally
			 */
			throw new ResponseNotReceivedException("No response was received in the waiting time of " + //
					maximumWaitingTimeInMilliseconds + " ms");
		});
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
		private CompletableFuture<Void> waitingFuture;
		
		private SelfRemovingMessageHandler(ClientMessageHandler<T> wrappedHandler, Class<T> responseType, CompletableFuture<Void> waitingFuture) {
			this.wrappedHandler = wrappedHandler;
			this.responseType = responseType;
			this.waitingFuture = waitingFuture;
		}
		
		@Override
		public void handleMessage(T message) {
			wrappedHandler.handleMessage(message);
			removeMessageHandler(responseType, this);
			
			waitingFuture.complete(null); // complete the future, so it stops waiting, after the response was received
		}
	}
}
