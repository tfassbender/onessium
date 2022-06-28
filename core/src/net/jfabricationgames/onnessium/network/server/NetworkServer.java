package net.jfabricationgames.onnessium.network.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.network.Network;

public class NetworkServer {
	
	// the server is not started with the LibGDX Framework, so it can use a logger
	private static final Logger log = LoggerFactory.getLogger(NetworkServer.class);
	
	@Inject
	private ServerMessageHandlerRegistry serverMessageHandlerRegistry;
	
	private Server server;
	
	public NetworkServer() {
		server = new Server() {
			@Override
			protected Connection newConnection() {
				return new NetworkConnection();
			}
		};
		
		server.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				// all connections are ChatConnections, because the method in the server is overwritten
				NetworkConnection networkConnection = (NetworkConnection) connection;
				serverMessageHandlerRegistry.handleMessage(networkConnection, object);
			}
			
			@Override
			public void disconnected(Connection connection) {
				// NetworkConnection chatConnection = (NetworkConnection) connection;
				//TODO logout
			}
		});
		
		Network.registerDtoClasses(server);
	}
	
	public void start(int port) throws IOException {
		log.info("Starting \"Onnessium\" server on port: {}", port);
		server.bind(port);
		server.start();
		log.info("Server successfully started on port: {}", port);
	}
}
