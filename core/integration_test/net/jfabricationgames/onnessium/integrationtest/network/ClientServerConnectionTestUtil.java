package net.jfabricationgames.onnessium.integrationtest.network;

import net.jfabricationgames.onnessium.network.client.Client;
import net.jfabricationgames.onnessium.network.server.Server;
import net.jfabricationgames.onnessium.util.Pair;
import net.jfabricationgames.onnessium.util.TestUtils;

public class ClientServerConnectionTestUtil {
	
	public static final String HOST = "localhost";
	public static final int PORT = 4742;
	
	private static Throwable clientConnectException;
	
	public static Pair<Client, Server> createConnection() throws Throwable {
		return createConnection(HOST, PORT);
	}
	
	public static Pair<Client, Server> createConnection(String host, int port) throws Throwable {
		Server server = new Server();
		Client client = new Client();
		
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
	public static void reduceConnectionTimeout() throws NoSuchFieldException, IllegalAccessException {
		TestUtils.setStaticFinalFieldPerReflection(Client.class, "DIRECT_RESPONSE_MAXIMUM_WAITING_TIME_IN_MILLISECONDS", 100);
	}
}
