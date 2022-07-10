package net.jfabricationgames.onnessium.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;

@ApplicationScoped
public class UserListManager {
	
	private static final Comparator<UserDto> ONLINE_FIRST = Comparator.comparing(dto -> dto.online);
	
	private List<UserDto> users;
	private List<UserListUpdateListener> updateListeners = new ArrayList<>();
	
	public List<UserDto> getUsers() {
		return users;
	}
	
	public void updateUserList(List<UserDto> users) {
		this.users = users;
		this.users.sort(ONLINE_FIRST);
		informListeners();
	}
	
	private void informListeners() {
		updateListeners.forEach(listener -> listener.onUserListUpdate(users));
	}
	
	public void addUpdateListener(UserListUpdateListener listener) {
		updateListeners.add(listener);
	}
	
	public void removeUpdateListener(UserListUpdateListener listener) {
		updateListeners.remove(listener);
	}
	
	/**
	 * A listener that wants to be informed about changes in the user list.
	 */
	public static interface UserListUpdateListener {
		
		public void onUserListUpdate(List<UserDto> newUserList);
	}
}
