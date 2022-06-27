package net.jfabricationgames.onnessium.network.client;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.jfabricationgames.cdi.annotation.Inject;

public class NetworkClientListener extends Listener {
	
	@Inject
	private ClientMessageHandlerRegistry clientMessageHandlerRegistry;
	
	@Override
	public void connected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "Connection established");
	}
	
	@Override
	public void received(Connection connection, Object object) {
		clientMessageHandlerRegistry.handleMessage(connection, object);
	}
	
	@Override
	public void disconnected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "Disconnected");
	}
}
