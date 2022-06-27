package net.jfabricationgames.onnessium.network.server;

public interface ServerMessageHandler<T> {
	
	public void handleMessage(NetworkConnection connection, T message);
}
