package net.jfabricationgames.onnessium.network.client;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;

import net.jfabricationgames.onnessium.network.network.Network;

public class NetworkClient {
	
	private Client client;
	
	public NetworkClient() {
		client = new Client();
		client.start();
		
		Network.registerDtoClasses(client);
		
		client.addListener(new NetworkClientListener());
	}
	
	public void connect() {
		new Thread(this::connectToServer, "connect").start();
	}
	
	private void connectToServer() {
		try {
			client.connect(5000, Network.HOST, Network.PORT);
		}
		catch (IOException e) {
			Gdx.app.error(getClass().getSimpleName(), "Connection cound not be established", e);
		}
	}
}
