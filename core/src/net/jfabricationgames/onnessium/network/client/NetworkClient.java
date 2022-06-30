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
	
	public NetworkClient() {
		client = new Client();
		client.start();
		
		Network.registerDtoClasses(client);
		
		client.addListener(new NetworkClientListener());
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
}
