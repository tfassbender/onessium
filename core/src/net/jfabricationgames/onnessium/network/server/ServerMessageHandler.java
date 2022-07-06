package net.jfabricationgames.onnessium.network.server;

public interface ServerMessageHandler<T> {
	
	public void handleMessage(Connection connection, T message);
}
