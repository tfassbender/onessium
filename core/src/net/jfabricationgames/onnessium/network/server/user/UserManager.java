package net.jfabricationgames.onnessium.network.server.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;

@ApplicationScoped
public class UserManager {
	
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);
	
	public static final String USER_FILE_DIRECTORY = "./config";
	public static final String USER_FILE_LOCATION = USER_FILE_DIRECTORY + "/users.json";
	
	private String userFile = USER_FILE_LOCATION; // can be changed in tests to not overwrite the configuration
	
	private Map<String, UserAccount> users;
	
	private Json json;
	
	public UserManager() {
		this(USER_FILE_LOCATION);
	}
	
	/**
	 * Used for testing, to not overwrite the server configuration.
	 */
	protected UserManager(String path) {
		this.userFile = path;
		json = new Json();
		loadUsers();
	}
	
	@SuppressWarnings("unchecked")
	private void loadUsers() {
		try (FileInputStream in = new FileInputStream(userFile)) {
			users = json.fromJson(HashMap.class, UserAccount.class, in);
		}
		catch (IOException e) {
			log.info("The users could not be loaded, because the users file does not exist.");
			users = new HashMap<>();
		}
	}
	
	public boolean isUserRegistered(String username) {
		return users.containsKey(username);
	}
	
	public boolean isLoginCorrect(String username, String password) {
		UserAccount user = users.get(username);
		String decryptedPassword = PasswordEncryptor.decrypt(user.encryptedPassword);
		return password.equals(decryptedPassword);
	}
	
	public void addUser(UserAccount user) {
		users.put(user.username, user);
		storeUsers();
	}
	
	private void storeUsers() {
		File directory = new File(USER_FILE_DIRECTORY);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		String serialized = json.prettyPrint(users);
		try (PrintWriter out = new PrintWriter(userFile)) {
			out.print(serialized);
		}
		catch (IOException e) {
			log.error("Couldn't write users to file.", e);
		}
	}
	
	public void setOnlineStateOf(String username, boolean online) {
		users.get(username).online = online;
	}
}