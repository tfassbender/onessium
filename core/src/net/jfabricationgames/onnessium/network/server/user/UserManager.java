package net.jfabricationgames.onnessium.network.server.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.network.dto.user.UserListDto;
import net.jfabricationgames.onnessium.network.server.Connection;
import net.jfabricationgames.onnessium.network.server.Server;
import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;

@ApplicationScoped
public class UserManager {
	
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);
	
	public static final String USER_FILE_DIRECTORY = "./config";
	public static final String USER_FILE_LOCATION = USER_FILE_DIRECTORY + "/users.json";
	
	@Inject
	private Server server;
	
	private String userFile; // can be changed in tests to not overwrite the configuration
	private Map<String, UserAccount> users;
	private Map<Connection, String> connectionToUsername = new HashMap<>();
	private Json json;
	
	public UserManager() {
		this(USER_FILE_LOCATION);
	}
	
	/**
	 * Used for testing, to not overwrite the server configuration.
	 */
	public UserManager(String path) {
		this.userFile = path;
		
		CdiContainer.injectTo(this);
		
		json = new Json(OutputType.json); // write normal JSON with all quotes
		json.setUsePrototypes(false); // write all fields (do not ignore default values)
		json.setTypeName(null); // do not include class names
		loadUsers();
	}
	
	@SuppressWarnings("unchecked")
	private void loadUsers() {
		try (FileInputStream in = new FileInputStream(userFile)) {
			users = json.fromJson(HashMap.class, UserAccount.class, in);
			users.values().forEach(user -> user.online = false);
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
	
	public void setOnlineStateOf(String username, boolean online, Connection connection) {
		if (username == null) {
			username = connectionToUsername.get(connection);
		}
		else {
			connectionToUsername.put(connection, username);
		}
		
		users.get(username).online = online;
		
		sendUserListToClients();
	}
	
	private void sendUserListToClients() {
		List<UserDto> userDtos = users.values().stream().map(UserAccount::toUserDto).collect(Collectors.toList());
		UserListDto userListDto = new UserListDto().setUsers(userDtos);
		server.broadcast(userListDto);
	}
}
