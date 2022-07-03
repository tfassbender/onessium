package net.jfabricationgames.onnessium.network.shared.exception;

public class ResponseNotReceivedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ResponseNotReceivedException(String message) {
		super(message);
	}
	
	public ResponseNotReceivedException(String message, Throwable cause) {
		super(message, cause);
	}
}
