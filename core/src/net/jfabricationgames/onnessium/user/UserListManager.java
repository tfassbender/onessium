package net.jfabricationgames.onnessium.user;

import java.util.ArrayList;
import java.util.List;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;

@ApplicationScoped
public class UserListManager {
	
	public List<UserDto> users;
	
	private List<UserListUpdateListener> updateListeners = new ArrayList<>();
	
	/**
	 * Call when the user list changes.
	 */
	public void onUpdate() {
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
