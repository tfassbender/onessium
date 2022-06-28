package net.jfabricationgames.onnessium.network.network;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	public static final int DEFAULT_PORT = 4711;
	public static final String DEFAULT_HOST = "localhost";
	
	private static List<Class<?>> registeredClasses = new ArrayList<>();
	
	/**
	 * Register DTOs centralised, so the implementation is shared in server and client.
	 */
	public static void registerDtoClasses(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		
		for (Class<?> clazz : registeredClasses) {
			kryo.register(clazz);
		}
	}
	
	public static void registerClass(Class<?> clazz) {
		registeredClasses.add(clazz);
	}
	
	private Network() {}
}
