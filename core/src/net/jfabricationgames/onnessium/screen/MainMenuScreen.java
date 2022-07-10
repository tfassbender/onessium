package net.jfabricationgames.onnessium.screen;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.user.UserListManager;
import net.jfabricationgames.onnessium.user.UserListManager.UserListUpdateListener;

public class MainMenuScreen extends MenuScreen implements UserListUpdateListener {
	
	@Inject
	private UserListManager userListManager;
	
	private com.badlogic.gdx.scenes.scene2d.ui.List<String> userList;
	private TextArea textAreaMessage;
	private Label labelChat;
	private ScrollPane scrollPaneChat;
	
	public MainMenuScreen() {
		// the dependency injection is done in the superclass MenuScreen
		
		updateUserList(userListManager.getUsers());
	}
	
	@Override
	public void show() {
		Table table = new Table();
		table.setFillParent(true);
		table.setDebug(true);
		stage.addActor(table);
		
		Skin skin = skinManager.getDefaultSkin();
		
		Table menuButtons = new Table();
		menuButtons.setFillParent(true);
		menuButtons.setDebug(true);
		
		TextButton createGame = new TextButton("Create Game", skin);
		TextButton joinGame = new TextButton("Join Game", skin);
		TextButton howToPlay = new TextButton("How To Play", skin);
		TextButton story = new TextButton("Story", skin);
		TextButton exit = new TextButton("Exit", skin);
		
		menuButtons.add(createGame);
		menuButtons.row();
		menuButtons.add(joinGame);
		menuButtons.row();
		menuButtons.add(howToPlay);
		menuButtons.row();
		menuButtons.add(story);
		menuButtons.row();
		menuButtons.add(exit);
		
		table.add(menuButtons);
		
		userList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
		ScrollPane scrollPaneUsers = new ScrollPane(userList, skin);
		table.add(scrollPaneUsers).fill();
		
		textAreaMessage = new TextArea("", skin);
		textAreaMessage.setPrefRows(3);
		textAreaMessage.addListener(new InputListener() {
			
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				boolean shiftKeyPressed = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
				boolean enterKeyPressed = event.getKeyCode() == Keys.ENTER;
				if (enterKeyPressed && !shiftKeyPressed) {
					sendMessage();
					return true;
				}
				
				return false;
			}
		});
		
		TextButton buttonSend = new TextButton("Send", skin);
		buttonSend.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				sendMessage();
			}
		});
		
		Table chat = new Table();
		chat.setFillParent(true);
		chat.setDebug(true);
		chat.add(textAreaMessage);
		chat.add(buttonSend);
		
		table.row();
		table.add(chat).colspan(2);
	}
	
	private void sendMessage() {
		String message = textAreaMessage.getText().trim();
		if (!message.isEmpty()) {
			//TODO send message
			appendChatText("", message);//TODO add the name of the logged in user
			textAreaMessage.setText("");
		}
	}
	
	private void appendChatText(String username, String message) {
		String text = labelChat.getText().toString();
		text += "\n" + username + ": " + message;
		labelChat.setText(text);
		
		scrollPaneChat.scrollTo(0, 0, 0, 0);
	}
	
	@Override
	public void onUserListUpdate(List<UserDto> newUserList) {
		updateUserList(newUserList);
	}
	
	private void updateUserList(List<UserDto> users) {
		Array<String> items = new Array<>();
		users.stream().map(dto -> dto.username).forEach(items::add);
		userList.setItems(items);
	}
}
