package net.jfabricationgames.onnessium.network.server.user;

import net.jfabricationgames.onnessium.network.dto.user.UserDto;

public class UserAccount {
	
	public String username;
	public String encryptedPassword;
	
	public boolean online;
	
	public UserAccount setUsername(String username) {
		this.username = username;
		return this;
	}
	
	public UserAccount setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
		return this;
	}
	
	public UserAccount setOnline(boolean online) {
		this.online = online;
		return this;
	}
	
	public UserDto toUserDto() {
		return new UserDto().setUsername(username).setOnline(online);
	}
}
