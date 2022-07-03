package net.jfabricationgames.onnessium;

import java.util.Arrays;
import java.util.List;

import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.user.dto.LoginDto;
import net.jfabricationgames.onnessium.user.dto.SignUpDto;

/**
 * Register all network classes in this central registry, to not create a dependency from the network package to every other package.
 */
public class NetworkDtoRegistry {
	
	private static final List<Class<?>> DTO_CLASSES = Arrays.asList(//
			// user
			LoginDto.class, //
			SignUpDto.class //
	);
	
	/**
	 * Register all DTOs in the Network class, to be able to serialise them and send them to the server / client.
	 */
	public static void initializeNetworkClasses() {
		for (Class<?> clazz : DTO_CLASSES) {
			Network.registerClass(clazz);
		}
	}
}
