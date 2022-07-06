package net.jfabricationgames.onnessium.network.dto.user;

import java.util.List;

public class UserListDto {
	
	public List<UserDto> users;
	
	public UserListDto setUsers(List<UserDto> users) {
		this.users = users;
		return this;
	}
}
