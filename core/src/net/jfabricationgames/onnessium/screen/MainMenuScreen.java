package net.jfabricationgames.onnessium.screen;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.network.dto.user.UserDto;
import net.jfabricationgames.onnessium.user.UserListManager;
import net.jfabricationgames.onnessium.user.UserListManager.UserListUpdateListener;

public class MainMenuScreen extends MenuScreen implements UserListUpdateListener {
	
	@Inject
	private UserListManager userListManager;
	
	private com.badlogic.gdx.scenes.scene2d.ui.List<String> userListOnline;
	private TextArea textAreaMessage;
	private Label chatLabel;
	private ScrollPane scrollPaneChat;
	
	public MainMenuScreen() {
		// the dependency injection is done in the superclass MenuScreen
		
		userListOnline = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skinManager.getDefaultSkin());
		updateUserList(userListManager.getUsers());
		userListManager.addUpdateListener(this);
	}
	
	@Override
	public void show() {
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		Skin skin = skinManager.getDefaultSkin();
		
		//**************************************************
		//*** Menu Buttons
		//**************************************************
		
		Table menuButtons = new Table();
		
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
		
		//**************************************************
		//*** User List
		//**************************************************
		
		Table users = new Table();
		
		Label usersLabel = new Label("users - online", skin);
		usersLabel.setFontScale(1.5f);
		ScrollPane scrollPaneUsers = new ScrollPane(userListOnline, skin);
		
		users.add(usersLabel);
		users.row();
		users.add(scrollPaneUsers).height(300);
		
		table.add(users).align(Align.top).width(350).padLeft(30);
		
		//**************************************************
		//*** Chat
		//**************************************************
		
		Label chatLabelHeader = new Label("Global Chat", skin);
		
		chatLabel = new Label("", skin);
		chatLabel.setWrap(true);
		chatLabel.getStyle().fontColor = Color.WHITE;
		scrollPaneChat = new ScrollPane(chatLabel, skin);
		
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
		chat.add(chatLabelHeader).colspan(2).align(Align.bottomLeft);
		chat.row();
		chat.add(scrollPaneChat).colspan(2).height(100).fill();
		chat.row();
		chat.add(textAreaMessage).width(500);
		chat.add(buttonSend);
		
		table.row();
		table.add(chat).colspan(2).padTop(20);
	}
	
	private void sendMessage() {
		String message = textAreaMessage.getText().trim();
		if (!message.isEmpty()) {
			//TODO send message
			appendChatText(userListManager.localUser.username, message);
			textAreaMessage.setText("");
		}
	}
	
	private void appendChatText(String username, String message) {
		String text = chatLabel.getText().toString();
		text += "\n" + username + ": " + message;
		chatLabel.setText(text);
		
		scrollPaneChat.scrollTo(0, 0, 0, 0);
	}
	
	@Override
	public void onUserListUpdate(List<UserDto> newUserList) {
		updateUserList(newUserList);
	}
	
	private void updateUserList(List<UserDto> users) {
		Array<String> items = new Array<>();
		users.stream().filter(dto -> dto.online).map(dto -> dto.username).forEach(items::add);
		userListOnline.setItems(items);
	}
}
