package net.jfabricationgames.onnessium.network.client;

import com.esotericsoftware.kryonet.Connection;

public interface ClientMessageHandler<T> {
	
	public void handleMessage(Connection connection, T message);
}
