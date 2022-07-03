package net.jfabricationgames.onnessium.network.server.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.onnessium.network.shared.PasswordEncryptor;

public class UserManagerTest {
	
	private static UserManager userManager;
	
	@BeforeAll
	public static void setup() throws NoSuchFieldException, IllegalAccessException {
		userManager = new UserManager("./config/users_tmp.json");
	}
	
	@Test
	public void testStoreAndLoadUsers() {
		// adding new users stores them automatically
		userManager.addUser(new UserAccount().setUsername("Arthur Dent").setEncryptedPassword(PasswordEncryptor.encrypt("secure_password_1!")));
		userManager.addUser(new UserAccount().setUsername("Trillian").setEncryptedPassword(PasswordEncryptor.encrypt("password_2")));
		
		// create a new user manager to load the users from file
		userManager = new UserManager("./config/users_tmp.json");
		
		assertTrue(userManager.isUserRegistered("Arthur Dent"));
		assertTrue(userManager.isUserRegistered("Trillian"));
		assertTrue(userManager.isLoginCorrect("Arthur Dent", "secure_password_1!"));
		assertTrue(userManager.isLoginCorrect("Trillian", "password_2"));
	}
}
