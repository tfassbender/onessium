package net.jfabricationgames.onnessium.user.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;

public class LastUsedClientSettings {
	
	public static final String LAST_USED_CLIENT_SETTINGS_DIRECTORY_PATH = System.getProperty("user.home") + "/.onnessium/config/";
	public static final String LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH = LAST_USED_CLIENT_SETTINGS_DIRECTORY_PATH + "last_used_client_settings.properties";
	
	private static final String PROPERTY_USERNAME = "username";
	private static final String PROPERTY_ENCRYPTED_PASSWORD = "encrypted_password";
	private static final String PROPERTY_HOST = "host";
	private static final String PROPERTY_PORT = "port";
	
	private static Properties defaultProperties = createDefaultProperties();
	
	private String username;
	private String password;
	private String encryptedPassword;
	private String host;
	private int port;
	
	private LastUsedClientSettings() {}
	
	private static Properties createDefaultProperties() {
		Properties properties = new Properties();
		
		properties.setProperty(PROPERTY_USERNAME, "Arthur Dent");
		properties.setProperty(PROPERTY_ENCRYPTED_PASSWORD, "");
		properties.setProperty(PROPERTY_HOST, Network.DEFAULT_HOST);
		properties.setProperty(PROPERTY_PORT, Integer.toString(Network.DEFAULT_PORT));
		
		return properties;
	}
	
	public static LastUsedClientSettings load() {
		Properties config = loadProperties();
		LastUsedClientSettings settings = new LastUsedClientSettings();
		
		settings.username = config.getProperty(PROPERTY_USERNAME, defaultProperties.getProperty(PROPERTY_USERNAME));
		settings.encryptedPassword = config.getProperty(PROPERTY_ENCRYPTED_PASSWORD, defaultProperties.getProperty(PROPERTY_ENCRYPTED_PASSWORD));
		settings.host = config.getProperty(PROPERTY_HOST, defaultProperties.getProperty(PROPERTY_HOST));
		String portAsString = config.getProperty(PROPERTY_PORT, defaultProperties.getProperty(PROPERTY_PORT));
		try {
			settings.port = Integer.parseInt(portAsString);
		}
		catch (NumberFormatException e) {
			Gdx.app.error(LastUsedClientSettings.class.getSimpleName(), "The port configuration '" + portAsString + "' could not be interpreted as a number. Using default port " + Network.DEFAULT_PORT, e);
			settings.port = Network.DEFAULT_PORT;
		}
		settings.password = PasswordEncryptor.decrypt(settings.encryptedPassword);
		
		return settings;
	}
	
	private static Properties loadProperties() {
		try {
			Properties config = new Properties();
			config.load(new FileInputStream(LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH));
			Gdx.app.log(LastUsedClientSettings.class.getSimpleName(), " *********************************************");
			Gdx.app.log(LastUsedClientSettings.class.getSimpleName(), " *** Client properties loaded successfully:");
			config.forEach((property, value) -> Gdx.app.log(LastUsedClientSettings.class.getSimpleName(), " *** " + property + ": " + value));
			Gdx.app.log(LastUsedClientSettings.class.getSimpleName(), " *********************************************");
			return config;
		}
		catch (FileNotFoundException e) {
			Gdx.app.log(LastUsedClientSettings.class.getSimpleName(), "Client properties not found - creating default properties file");
			createConfigFile(defaultProperties);
			return defaultProperties;
		}
		catch (IOException e) {
			Gdx.app.error(LastUsedClientSettings.class.getSimpleName(), "Client properties couldn't be loaded. Using default properties. ", e);
			return defaultProperties;
		}
	}
	
	private static void createConfigFile(Properties config) {
		File directory = new File(LAST_USED_CLIENT_SETTINGS_DIRECTORY_PATH);
		File properties = new File(LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH);
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (!properties.exists()) {
			try {
				properties.createNewFile();
				config.store(new FileOutputStream(properties), "Properties for the Onnessium Client");
			}
			catch (IOException e) {
				Gdx.app.error(LastUsedClientSettings.class.getSimpleName(), "Could not create properties file", e);
			}
		}
	}
	
	public static void store(String username, String password, String host, int port) {
		String encryptedPassword = PasswordEncryptor.encrypt(password);
		
		Properties config = new Properties();
		config.setProperty(PROPERTY_USERNAME, username);
		config.setProperty(PROPERTY_ENCRYPTED_PASSWORD, encryptedPassword);
		config.setProperty(PROPERTY_HOST, host);
		config.setProperty(PROPERTY_PORT, Integer.toString(port));
		
		File propertiesFile = new File(LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH);
		if (!propertiesFile.exists()) {
			createConfigFile(config);
		}
		else {
			try {
				config.store(new FileOutputStream(propertiesFile), "Properties for the Onnessium Client");
			}
			catch (IOException e) {
				Gdx.app.error(LastUsedClientSettings.class.getSimpleName(), "Could not update client properties", e);
			}
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
}
