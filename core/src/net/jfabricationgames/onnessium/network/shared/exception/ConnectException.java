package net.jfabricationgames.onnessium.network.shared.exception;

public class ConnectException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}
}
