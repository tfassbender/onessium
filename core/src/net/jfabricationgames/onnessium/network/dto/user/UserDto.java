package net.jfabricationgames.onnessium.network.dto.user;

public class UserDto {
	
	public String username;
	public boolean online;
	
	public UserDto setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public UserDto setOnline(boolean online) {
		this.online = online;
		return this;
	}
}
