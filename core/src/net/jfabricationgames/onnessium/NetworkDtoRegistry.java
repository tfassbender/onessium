package net.jfabricationgames.onnessium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.maps.Map;

import net.jfabricationgames.onnessium.network.dto.chat.ChatMessageDto;
import net.jfabricationgames.onnessium.network.dto.user.LoginDto;
import net.jfabricationgames.onnessium.network.dto.user.SignUpDto;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.network.dto.user.UserListDto;
import net.jfabricationgames.onnessium.network.shared.Network;

/**
 * Register all network classes in this central registry, to not create a dependency from the network package to every other package.
 */
public class NetworkDtoRegistry {
	
	private static final List<Class<?>> DTO_CLASSES = Arrays.asList(//
			// user
			LoginDto.class, //
			SignUpDto.class, //
			UserDto.class, //
			UserListDto.class, //
			
			// chat
			ChatMessageDto.class, //
			
			// common
			List.class, //
			ArrayList.class, //
			Map.class, //
			HashMap.class //
	);
	
	/**
	 * Register all DTOs in the Network class, to be able to serialise them and send them to the server / client.
	 */
	public static void initializeNetworkClasses() {
		for (Class<?> clazz : DTO_CLASSES) {
			Network.registerDtoClass(clazz);
		}
	}
}
