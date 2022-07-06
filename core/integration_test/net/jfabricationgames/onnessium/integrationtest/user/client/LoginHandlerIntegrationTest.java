package net.jfabricationgames.onnessium.integrationtest.user.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.integrationtest.network.ClientServerConnectionTestUtil;
import net.jfabricationgames.onnessium.network.client.NetworkClient;
import net.jfabricationgames.onnessium.network.dto.user.LoginDto;
import net.jfabricationgames.onnessium.network.dto.user.SignUpDto;
import net.jfabricationgames.onnessium.network.server.NetworkServer;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandlerRegistry;
import net.jfabricationgames.onnessium.network.server.handler.LoginServerHandler;
import net.jfabricationgames.onnessium.network.server.handler.SignUpServerHandler;
import net.jfabricationgames.onnessium.network.server.user.UserManager;
import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettingsTest;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettingsTestUtil;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler.LoginException;
import net.jfabricationgames.onnessium.util.Pair;
import net.jfabricationgames.onnessium.util.TestUtils;
import net.jfabricationgames.onnessium.util.Wrapper;

public class LoginHandlerIntegrationTest {
	
	private static NetworkServer server;
	private static NetworkClient client;
	
	private LoginHandler loginHandler = new LoginHandler();
	
	@Inject
	private ServerMessageHandlerRegistry handlerRegistry;
	
	@BeforeAll
	public static void setup() throws Exception {
		TestUtils.mockGdxApplication();
		TestUtils.createCdiContainer();
		
		LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(LastUsedClientSettingsTest.TEMPORARY_SETTINGS_FILE_PATH);
		
		Network.registerClass(LoginDto.class);
		Network.registerClass(SignUpDto.class);
	}
	
	@BeforeEach
	public void injectDependenciesAndResetHandlers() throws Throwable {
		CdiContainer.injectTo(this);
		
		client = new NetworkClient();
		
		ClientServerConnectionTestUtil.reduceConnectionTimeout();
		
		handlerRegistry.removeAllHandlers();
		client.removeAllMessageHandlersForType(LoginDto.class);
		client.removeAllMessageHandlersForType(SignUpDto.class);
		
		server = new NetworkServer();
		server.start(ClientServerConnectionTestUtil.PORT);
		
		TestUtils.setFieldPerReflection(loginHandler, "responseWaitingTimeInMilliseconds", 10);
	}
	
	@AfterEach
	public void disconnect() throws NoSuchFieldException, IllegalAccessException {
		client.disconnect();
		server.stop();
		
		TestUtils.setFieldPerReflection(loginHandler, "responseWaitingTimeInMilliseconds", 5000);
	}
	
	@AfterAll
	public static void restoreExistingConfigFile() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		LastUsedClientSettingsTestUtil.resetSettingsPropertyPath();
		
		new File(LastUsedClientSettingsTest.TEMPORARY_SETTINGS_FILE_PATH).delete();
	}
	
	@Test
	public void testSignUpSuccessful() throws LoginException {
		handlerRegistry.addHandler(SignUpDto.class, (connection, dto) -> {
			dto.setSuccessful(true); // respond that the sign up was successful
			connection.sendTCP(dto);
		});
		
		Wrapper<Boolean> responseWrapper = Wrapper.empty();
		loginHandler.signUp("Arthur Dent", "42", ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> responseWrapper.wrapped = true);
	}
	
	@Test
	public void testSignUpNotSuccessful() throws LoginException {
		String errorMessage = "Signup not possible";
		handlerRegistry.addHandler(SignUpDto.class, (connection, dto) -> {
			dto.setSuccessful(false); // respond that the sign up was NOT successful
			dto.setErrorMessage(errorMessage);
			connection.sendTCP(dto);
		});
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.signUp("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals(errorMessage, loginException.getMessage());
	}
	
	@Test
	public void testSignUpNotSuccessfulBecauseServerIsNotStarted() throws LoginException {
		// the server is already started, so shut it down
		server.stop();
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.signUp("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("Sign up failed - Cannot connect to server", loginException.getMessage());
	}
	
	@Test
	public void testSignUpNotSuccessfulBecauseServerDoesNotRespond() throws LoginException {
		// do not respond to the login request
		handlerRegistry.addHandler(SignUpDto.class, (connection, dto) -> {});
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.signUp("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("Sign up failed - The server is not responding", loginException.getMessage());
	}
	
	@Test
	public void testSignUpNotSuccessfulBecauseTheUsernameIsAlreadyRegistered() throws LoginException, NoSuchFieldException, IllegalAccessException {
		SignUpServerHandler signUpHandler = createSignUpHandlerWithMockedUserManager().getKey();
		handlerRegistry.addHandler(SignUpDto.class, signUpHandler);
		
		String username = "unique_username_42";
		String password = "password";
		createTestUser(username, password);
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.signUp(username, password, //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("A user with the name \"" + username + "\" is already registered on this server.", loginException.getMessage());
	}
	
	@Test
	public void testSignUpNotSuccessfulBecauseThePasswordIsNotLongEnough() throws LoginException, NoSuchFieldException, IllegalAccessException {
		SignUpServerHandler signUpHandler = createSignUpHandlerWithMockedUserManager().getKey();
		handlerRegistry.addHandler(SignUpDto.class, signUpHandler);
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.signUp("user_with_to_short_password", "1234", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("The password must be at least 5 characters long.", loginException.getMessage());
	}
	
	@Test
	public void testLoginSuccessful() throws LoginException {
		handlerRegistry.addHandler(LoginDto.class, (connection, dto) -> {
			dto.setSuccessful(true); // respond that the login was successful
			connection.sendTCP(dto);
		});
		
		Wrapper<Boolean> responseWrapper = Wrapper.empty();
		loginHandler.login("Arthur Dent", "42", ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> responseWrapper.wrapped = true);
	}
	
	@Test
	public void testLoginNotSuccessful() throws LoginException {
		String errorMessage = "Login not possible";
		handlerRegistry.addHandler(LoginDto.class, (connection, dto) -> {
			dto.setSuccessful(false); // respond that the login was NOT successful
			dto.setErrorMessage(errorMessage);
			connection.sendTCP(dto);
		});
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.login("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals(errorMessage, loginException.getMessage());
	}
	
	@Test
	public void testLoginNotSuccessfulBecauseServerIsNotStarted() throws LoginException {
		// the server is already started, so shut it down
		server.stop();
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.login("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("Login failed - Cannot connect to server", loginException.getMessage());
	}
	
	@Test
	public void testLoginNotSuccessfulBecauseServerDoesNotRespond() throws LoginException {
		// do not respond to the login request
		handlerRegistry.addHandler(LoginDto.class, (connection, dto) -> {});
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.login("Arthur Dent", "42", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("Login failed - The server is not responding", loginException.getMessage());
	}
	
	@Test
	public void testLoginNotSuccessfulBecauseUserIsNotRegistered() throws LoginException, NoSuchFieldException, IllegalAccessException {
		LoginServerHandler loginServerHandler = createLoginHandlerWithMockedUserManager().getKey();
		handlerRegistry.addHandler(LoginDto.class, loginServerHandler);
		
		String username = "not_existing_username";
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.login(username, "password", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("A user with the name \"" + username + "\" is not registered on this server.", loginException.getMessage());
	}
	
	@Test
	public void testLoginNotSuccessfulBecausePasswordIsWrong() throws LoginException, NoSuchFieldException, IllegalAccessException, InterruptedException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Pair<LoginServerHandler, UserManager> loginServerHandler = createLoginHandlerWithMockedUserManager();
		handlerRegistry.addHandler(LoginDto.class, loginServerHandler.getKey());
		
		String username = "user_with_password";
		String password = "correct_password";
		createTestUser(username, password);
		
		// give the server some time to handle the sign up of the user
		Thread.sleep(100);
		// reload the users, because the login handler uses a different instance of the user manager (because they are mocked)
		TestUtils.invokePrivateMethod(loginServerHandler.getValue(), "loadUsers");
		
		LoginException loginException = assertThrows(LoginException.class, () -> loginHandler.login(username, "wrong_password", //
				ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, //
				() -> {}));
		assertEquals("The password for user \"" + username + "\" is not correct.", loginException.getMessage());
	}
	
	private Pair<SignUpServerHandler, UserManager> createSignUpHandlerWithMockedUserManager() throws NoSuchFieldException, IllegalAccessException {
		SignUpServerHandler handler = new SignUpServerHandler();
		UserManager mocked = new UserManager("./config/users_tmp.json");
		TestUtils.setFieldPerReflection(handler, "userManager", mocked);
		
		return Pair.of(handler, mocked);
	}
	
	private Pair<LoginServerHandler, UserManager> createLoginHandlerWithMockedUserManager() throws NoSuchFieldException, IllegalAccessException {
		LoginServerHandler handler = new LoginServerHandler();
		UserManager mocked = new UserManager("./config/users_tmp.json");
		TestUtils.setFieldPerReflection(handler, "userManager", mocked);
		
		return Pair.of(handler, mocked);
	}
	
	private void createTestUser(String username, String password) throws NoSuchFieldException, IllegalAccessException {
		// create a sign up handler, enable the server to store the signed up user
		SignUpServerHandler signUpHandler = createSignUpHandlerWithMockedUserManager().getKey();
		handlerRegistry.addHandler(SignUpDto.class, signUpHandler);
		
		try {
			loginHandler.signUp(username, password, ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, () -> {});
		}
		catch (LoginException e) {
			// the first sign up may not work, because the user file was not deleted
		}
	}
}
