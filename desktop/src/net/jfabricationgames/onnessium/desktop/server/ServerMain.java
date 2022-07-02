package net.jfabricationgames.onnessium.desktop.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.onnessium.NetworkDtoRegistry;
import net.jfabricationgames.onnessium.network.network.Network;
import net.jfabricationgames.onnessium.network.server.NetworkServer;

public class ServerMain {
	
	private static final Logger log = LoggerFactory.getLogger(ServerMain.class);
	
	public static final String SERVER_PROPERTIES_DIRECTORY_PATH = "./config";
	public static final String SERVER_PROPERTIES_PATH = SERVER_PROPERTIES_DIRECTORY_PATH + "/server_config.properties";
	public static final String SERVER_PROPERTY_PORT = "port";
	
	public static void main(String[] args) throws IOException {
		initializeCdiContainer();
		initializeNetworkClasses();
		
		Properties config = loadOrCreateServerConfig();
		
		int port = Network.DEFAULT_PORT;
		String portProperty = config.getProperty(SERVER_PROPERTY_PORT, Integer.toString(Network.DEFAULT_PORT));
		try {
			port = Integer.parseInt(portProperty);
		}
		catch (NumberFormatException e) {
			log.debug("The port configuration '" + portProperty + "' could not be interpreted as a number. Using default port " + Network.DEFAULT_PORT);
		}
		
		NetworkServer server = new NetworkServer();
		server.start(port);
		
		Thread.setDefaultUncaughtExceptionHandler(new ServerGlobalExceptionHandler());
	}
	
	private static void initializeCdiContainer() throws IOException {
		CdiContainer.create("net.jfabricationgames.onnessium");
	}
	
	private static void initializeNetworkClasses() {
		NetworkDtoRegistry.initializeNetworkClasses();
	}
	
	private static Properties loadOrCreateServerConfig() throws IOException {
		Properties config = new Properties();
		try {
			config.load(new FileInputStream(SERVER_PROPERTIES_PATH));
			log.info(" ************************************************");
			log.info(" *** server properties loaded successfully:");
			config.forEach((key, value) -> log.info(" *** {}: {}", key, value));
			log.info(" ************************************************");
		}
		catch (FileNotFoundException e) {
			log.info("server properties not found - creating default properties file");
			config = new Properties();
			config.put(SERVER_PROPERTY_PORT, Integer.toString(Network.DEFAULT_PORT));
			createConfigFile(config);
		}
		catch (IOException e) {
			log.error("server properties couldn't be loaded: ", e);
			throw e;
		}
		
		return config;
	}
	
	private static void createConfigFile(Properties config) throws IOException {
		File directory = new File(SERVER_PROPERTIES_DIRECTORY_PATH);
		File properties = new File(SERVER_PROPERTIES_PATH);
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (!properties.exists()) {
			try {
				properties.createNewFile();
				config.store(new FileOutputStream(properties), "Properties for the Onnessium Server");
			}
			catch (IOException e) {
				log.error("could not create properties file", e);
				throw e;
			}
		}
	}
	
	public static class ServerGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
		
		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			log.error("SERVER_UNCAUGHT_EXCEPTION", "Uncaught exception in thread [{}]: {}", thread.getName(), throwable);
		}
	}
}
