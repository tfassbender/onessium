package net.jfabricationgames.onnessium.desktop.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {
	
	private static final Logger log = LoggerFactory.getLogger(ServerMain.class);
	
	public static void main(String[] args) throws IOException {
		// TODO
		// ChatServer server = new ChatServer();
		// server.start();
		
		Thread.setDefaultUncaughtExceptionHandler(new ServerGlobalExceptionHandler());
	}
	
	public static class ServerGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
		
		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			log.error("SERVER_UNCAUGHT_EXCEPTION", "Uncaught exception in thread [{}]: {}", thread.getName(), throwable);
		}
	}
}
