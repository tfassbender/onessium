package net.jfabricationgames.onnessium.network.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Listener;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.dto.user.LogoutDto;
import net.jfabricationgames.onnessium.network.shared.Network;

@ApplicationScoped
public class Server {
	
	// the server is not started with the LibGDX Framework, so it can use a logger
	private static final Logger log = LoggerFactory.getLogger(Server.class);
	
	@Inject
	private ServerMessageHandlerRegistry serverMessageHandlerRegistry;
	
	private com.esotericsoftware.kryonet.Server server;
	
	public Server() {
		CdiContainer.injectTo(this);
		
		server = new com.esotericsoftware.kryonet.Server() {
			@Override
			protected com.esotericsoftware.kryonet.Connection newConnection() {
				return new Connection();
			}
		};
		
		server.addListener(new Listener() {
			@Override
			public void received(com.esotericsoftware.kryonet.Connection con, Object object) {
				// all connections are our connection type, because the method in the server is overwritten
				Connection connection = (Connection) con;
				serverMessageHandlerRegistry.handleMessage(connection, object);
			}
			
			@Override
			public void disconnected(com.esotericsoftware.kryonet.Connection connection) {
				Connection chatConnection = (Connection) connection;
				serverMessageHandlerRegistry.handleMessage(chatConnection, new LogoutDto());
			}
		});
		
		Network.registerDtoClassesInEndpoint(server);
	}
	
	public void start(int port) throws IOException {
		log.info("Starting \"Onnessium\" server on port: {}", port);
		server.bind(port);
		server.start();
		log.info("Server successfully started on port: {}", port);
	}
	
	public void stop() {
		log.info("Stopping \"Onnessium\" server.");
		server.stop();
	}
	
	/**
	 * Send a message object to all clients that are connected.
	 * 
	 * NOTE: The type of the message that is sent has to be registered. See {@link Network#registerDtoClass(Class)} 
	 * and NetworkDtoRegistry.
	 */
	public void broadcast(Object message) {
		server.sendToAllTCP(message);
	}
}
