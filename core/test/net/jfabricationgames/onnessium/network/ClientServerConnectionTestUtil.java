package net.jfabricationgames.onnessium.network;

import java.lang.reflect.Field;

import net.jfabricationgames.onnessium.network.client.NetworkClient;
import net.jfabricationgames.onnessium.network.server.NetworkServer;
import net.jfabricationgames.onnessium.util.Pair;

public class ClientServerConnectionTestUtil {
	
	public static final String HOST = "localhost";
	public static final int PORT = 4742;
	
	private static Throwable clientConnectException;
	
	public static Pair<NetworkClient, NetworkServer> createConnection() throws Throwable {
		return createConnection(HOST, PORT);
	}
	
	public static Pair<NetworkClient, NetworkServer> createConnection(String host, int port) throws Throwable {
		NetworkServer server = new NetworkServer();
		NetworkClient client = new NetworkClient();
		
		clientConnectException = null;
		
		server.start(PORT);
		client.connect(HOST, PORT) //
				.exceptionally(t -> {
					clientConnectException = t;
					return null;
				}) //
				.thenAccept(v -> {}) //
				.get();
		
		if (clientConnectException != null) {
			throw clientConnectException;
		}
		
		return Pair.of(client, server);
	}
	
	/**
	 * Reduce the connection timeout to 100 milliseconds, so the test doesn't take too long.
	 */
	public static void reduceConnectionTimeout(NetworkClient client) throws NoSuchFieldException, IllegalAccessException {
		Field field = NetworkClient.class.getDeclaredField("connectionTimeoutInMilliseconds");
		field.setAccessible(true);
		field.set(client, 100);
		field.setAccessible(false);
	}
}
