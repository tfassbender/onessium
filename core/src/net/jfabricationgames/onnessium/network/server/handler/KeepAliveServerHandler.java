package net.jfabricationgames.onnessium.network.server.handler;

import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;

import net.jfabricationgames.onnessium.network.server.Connection;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandler;

/**
 * A message handler that handles KeepAlive messages, so they are not logged as unknown message.
 */
public class KeepAliveServerHandler implements ServerMessageHandler<KeepAlive> {
	
	@Override
	public void handleMessage(Connection connection, KeepAlive message) {
		// do nothing here
	}
}
