package net.jfabricationgames.onnessium.chat.dto;

public class ChatMessageDto {
	
	public String username;
	public String message;
	
	public ChatMessageDto setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public ChatMessageDto setMessage(String message) {
		this.message = message;
		return this;
	}
}
