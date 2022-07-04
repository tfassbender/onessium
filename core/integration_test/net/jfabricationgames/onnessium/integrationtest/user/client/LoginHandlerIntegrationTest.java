package net.jfabricationgames.onnessium.integrationtest.user.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

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
import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettings;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettingsTest;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler.LoginException;
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
		
		TestUtils.setStaticFieldPerReflection(LastUsedClientSettings.class, "SETTINGS_PROPERTY_PATH", LastUsedClientSettingsTest.TEMPORARY_SETTINGS_FILE_PATH);
		
		Network.registerClass(LoginDto.class);
		Network.registerClass(SignUpDto.class);
	}
	
	@BeforeEach
	public void injectDependenciesAndResetHandlers() throws Throwable {
		CdiContainer.injectTo(this);
		
		client = new NetworkClient();
		
		ClientServerConnectionTestUtil.reduceConnectionTimeout(client);
		
		handlerRegistry.removeAllHandlers();
		client.removeAllMessageHandlersForType(LoginDto.class);
		client.removeAllMessageHandlersForType(SignUpDto.class);
		
		server = new NetworkServer();
		server.start(ClientServerConnectionTestUtil.PORT);
		
		TestUtils.setFieldPerReflection(LoginHandler.class, loginHandler, "responseWaitingTimeInMilliseconds", 10);
	}
	
	@AfterEach
	public void disconnect() throws NoSuchFieldException, IllegalAccessException {
		client.disconnect();
		server.stop();
		
		TestUtils.setFieldPerReflection(LoginHandler.class, loginHandler, "responseWaitingTimeInMilliseconds", 5000);
	}
	
	@AfterAll
	public static void restoreExistingConfigFile() throws NoSuchFieldException, IllegalAccessException {
		TestUtils.setStaticFieldPerReflection(LastUsedClientSettings.class, "SETTINGS_PROPERTY_PATH", LastUsedClientSettings.LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH);
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
}
