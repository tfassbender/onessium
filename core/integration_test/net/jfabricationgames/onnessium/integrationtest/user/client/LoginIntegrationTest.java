package net.jfabricationgames.onnessium.integrationtest.user.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.ClientHandlerRegistry;
import net.jfabricationgames.onnessium.NetworkDtoRegistry;
import net.jfabricationgames.onnessium.integrationtest.network.ClientServerConnectionTestUtil;
import net.jfabricationgames.onnessium.network.client.Client;
import net.jfabricationgames.onnessium.network.dto.user.LoginDto;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.network.server.Server;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandlerRegistry;
import net.jfabricationgames.onnessium.network.server.handler.LoginServerHandler;
import net.jfabricationgames.onnessium.network.server.user.UserManager;
import net.jfabricationgames.onnessium.user.UserListManager;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettingsTest;
import net.jfabricationgames.onnessium.user.client.LastUsedClientSettingsTestUtil;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler.LoginException;
import net.jfabricationgames.onnessium.util.Pair;
import net.jfabricationgames.onnessium.util.TestUtils;
import net.jfabricationgames.onnessium.util.Wrapper;

/**
 * Test the complete process of logging in a user and receiving the new user list.
 * This class tests only the happy path of the login. Errors are tested in {@link LoginHandlerIntegrationTest}, because these tests 
 * need a different setup.
 */
public class LoginIntegrationTest {
	
	@Inject
	private static Server server;
	@Inject
	private static Client client;
	@Inject
	private ServerMessageHandlerRegistry handlerRegistry;
	
	@Inject
	private UserListManager userListManager;
	
	private LoginHandler loginHandler = new LoginHandler();
	
	@BeforeAll
	public static void setup() throws Exception {
		TestUtils.mockGdxApplication();
		TestUtils.createCdiContainer();
		
		LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(LastUsedClientSettingsTest.TEMPORARY_SETTINGS_FILE_PATH);
		
		NetworkDtoRegistry.initializeNetworkClasses();
		ClientHandlerRegistry.initializeClientHandlers();
	}
	
	@BeforeEach
	public void injectDependenciesAndResetHandlers() throws Throwable {
		CdiContainer.injectTo(this);
		
		ClientServerConnectionTestUtil.reduceConnectionTimeout();
		handlerRegistry.removeAllHandlers();
		server.start(ClientServerConnectionTestUtil.PORT);
		TestUtils.setFieldPerReflection(loginHandler, "responseWaitingTimeInMilliseconds", 50);
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
	
	/**
	 * NOTE: This test might fail in about 10% of test runs for unknown reasons.
	 */
	@Test
	public void testReceiveUserListAfterLogin() throws LoginException, InterruptedException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Pair<LoginServerHandler, UserManager> loginServerHandler = LoginTestUtils.createLoginHandlerWithMockedUserManager();
		handlerRegistry.addHandler(LoginDto.class, loginServerHandler.getKey());
		
		String username = "Arthur_Dent_unique_cvh38ch3";
		String password = "secure_password_1!";
		LoginTestUtils.createTestUser(username, password, handlerRegistry, loginHandler);
		
		// give the server some time to handle the sign up of the user
		Thread.sleep(10);
		// reload the users, because the login handler uses a different instance of the user manager (because they are mocked)
		TestUtils.invokePrivateMethod(loginServerHandler.getValue(), "loadUsers");
		
		// set up the handler for the user list, that collects the user list and sends an update to all registered listeners
		Wrapper<List<UserDto>> userListWrapper = Wrapper.empty();
		userListManager.addUpdateListener(listUpdate -> userListWrapper.wrapped = listUpdate);
		
		loginHandler.login(username, password, ClientServerConnectionTestUtil.HOST, ClientServerConnectionTestUtil.PORT, () -> {});
		
		Thread.sleep(50); // the user list is sent asynchronous, so wait for it to arrive
		
		assertFalse(userListWrapper.isEmpty());
		assertFalse(userListWrapper.wrapped.isEmpty());
		assertTrue(userListWrapper.wrapped.stream().anyMatch(user -> user.username.equals(username) && user.online));
	}
}
