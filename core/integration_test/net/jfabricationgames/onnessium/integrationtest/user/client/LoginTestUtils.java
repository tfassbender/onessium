package net.jfabricationgames.onnessium.integrationtest.user.client;

import net.jfabricationgames.onnessium.integrationtest.network.ClientServerConnectionTestUtil;
import net.jfabricationgames.onnessium.network.dto.user.SignUpDto;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandlerRegistry;
import net.jfabricationgames.onnessium.network.server.handler.LoginServerHandler;
import net.jfabricationgames.onnessium.network.server.handler.SignUpServerHandler;
import net.jfabricationgames.onnessium.network.server.user.UserManager;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler.LoginException;
import net.jfabricationgames.onnessium.util.Pair;
import net.jfabricationgames.onnessium.util.TestUtils;

public class LoginTestUtils {

	public static Pair<SignUpServerHandler, UserManager> createSignUpHandlerWithMockedUserManager() throws NoSuchFieldException, IllegalAccessException {
		SignUpServerHandler handler = new SignUpServerHandler();
		UserManager mocked = new UserManager("./config/users_tmp.json");
		TestUtils.setFieldPerReflection(handler, "userManager", mocked);
		
		return Pair.of(handler, mocked);
	}

	public static Pair<LoginServerHandler, UserManager> createLoginHandlerWithMockedUserManager() throws NoSuchFieldException, IllegalAccessException {
		LoginServerHandler handler = new LoginServerHandler();
		UserManager mocked = new UserManager("./config/users_tmp.json");
		TestUtils.setFieldPerReflection(handler, "userManager", mocked);
		
		return Pair.of(handler, mocked);
	}

	public static void createTestUser(String username, String password, ServerMessageHandlerRegistry handlerRegistry, LoginHandler loginHandler) throws NoSuchFieldException, IllegalAccessException {
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
