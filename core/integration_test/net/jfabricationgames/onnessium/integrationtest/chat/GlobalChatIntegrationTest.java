package net.jfabricationgames.onnessium.integrationtest.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.NetworkDtoRegistry;
import net.jfabricationgames.onnessium.chat.ChatClientManager;
import net.jfabricationgames.onnessium.chat.ChatMessageClientHandler;
import net.jfabricationgames.onnessium.chat.dto.ChatMessageDto;
import net.jfabricationgames.onnessium.integrationtest.network.ClientServerConnectionTestUtil;
import net.jfabricationgames.onnessium.integrationtest.user.client.LoginTestUtils;
import net.jfabricationgames.onnessium.network.client.Client;
import net.jfabricationgames.onnessium.network.server.Server;
import net.jfabricationgames.onnessium.network.server.ServerMessageHandlerRegistry;
import net.jfabricationgames.onnessium.network.server.handler.ChatMessageServerHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.util.TestUtils;
import net.jfabricationgames.onnessium.util.Wrapper;

public class GlobalChatIntegrationTest {
	
	@Inject
	private Server server;
	@Inject
	private ServerMessageHandlerRegistry serverMessageHandlerRegistry;
	@Inject
	private Client client1;
	@Inject
	private ChatClientManager chatClientManager;
	
	// the second client cannot be injected, because we need a different client
	private Client client2;
	
	private LoginHandler loginHandler1;
	private LoginHandler loginHandler2;
	
	@BeforeEach
	public void setup() throws Exception {
		TestUtils.mockGdxApplication();
		TestUtils.createCdiContainer();
		
		NetworkDtoRegistry.initializeNetworkClasses();
		
		CdiContainer.injectTo(this);
		
		serverMessageHandlerRegistry.addHandler(ChatMessageDto.class, new ChatMessageServerHandler());
		
		ClientServerConnectionTestUtil.reduceConnectionTimeout();
		server.start(ClientServerConnectionTestUtil.PORT);
		
		client2 = new Client();
		
		loginHandler1 = new LoginHandler();
		loginHandler2 = new LoginHandler();
		TestUtils.setFieldPerReflection(loginHandler2, "client", client2);
		
		LoginTestUtils.createTestUser("chat_user_1", "secure_password_1!", serverMessageHandlerRegistry, loginHandler1);
		LoginTestUtils.createTestUser("chat_user_2", "secure_password_1!", serverMessageHandlerRegistry, loginHandler2);
	}
	
	@AfterEach
	public void tearDown() {
		CdiContainer.destroy();
	}
	
	@Test
	public void testGlobalChat() throws InterruptedException {
		client1.addMessageHandler(ChatMessageDto.class, new ChatMessageClientHandler());
		Wrapper<ChatMessageDto> chatMessageWrapper1 = Wrapper.empty();
		chatClientManager.addChatMessageListener(message -> chatMessageWrapper1.wrapped = message);
		
		Wrapper<ChatMessageDto> chatMessageWrapper2 = Wrapper.empty();
		client2.addMessageHandler(ChatMessageDto.class, message -> chatMessageWrapper2.wrapped = message);
		
		// test sending a message from client_1 to client_2 
		
		String user1 = "chat_user_1";
		String message1 = "test message from user 1";
		client1.send(new ChatMessageDto().setUsername(user1).setMessage(message1));
		
		Thread.sleep(10); // wait for the server to respond
		
		assertNotNull(chatMessageWrapper2.wrapped, "The second client is expected to receive the chat message.");
		assertNull(chatMessageWrapper1.wrapped, "The first client is not expected to receive it's own chat message.");
		
		assertEquals(user1, chatMessageWrapper2.wrapped.username);
		assertEquals(message1, chatMessageWrapper2.wrapped.message);
		
		// test the other way of chat message
		
		chatMessageWrapper1.wrapped = null;
		chatMessageWrapper2.wrapped = null;
		
		String user2 = "chat_user_2";
		String message2 = "response message from user 2";
		client2.send(new ChatMessageDto().setUsername(user2).setMessage(message2));
		
		Thread.sleep(10); // wait for the server to respond
		
		assertNull(chatMessageWrapper2.wrapped, "The second client is not expected to receive it's own chat message.");
		assertNotNull(chatMessageWrapper1.wrapped, "The first client is expected to receive the chat message.");
		
		assertEquals(user2, chatMessageWrapper1.wrapped.username);
		assertEquals(message2, chatMessageWrapper1.wrapped.message);
	}
}
