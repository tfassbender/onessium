package net.jfabricationgames.onnessium.network.client;

public interface ClientMessageHandler<T> {
	
	public void handleMessage(T message);
}
