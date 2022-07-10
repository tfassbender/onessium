package net.jfabricationgames.onnessium.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.onnessium.user.client.LastUsedClientSettings;
import net.jfabricationgames.onnessium.user.client.LoginHandler;
import net.jfabricationgames.onnessium.user.client.LoginHandler.LoginException;

public class LoginScreen extends MenuScreen {
	
	private LastUsedClientSettings clientSettings;
	private LoginHandler loginHandler;
	
	private TextField name;
	private TextField password;
	private TextField host;
	private TextField port;
	
	private Label error;
	/**
	 * The flag is needed to be able to change to the next screen from the thread, that executes the rendering.
	 * When calling the setScreen method from within the runnable in the login method, it will lead to a LWJGL problem.
	 */
	private boolean changeToMainMenuScreen;
	
	public LoginScreen() {
		clientSettings = LastUsedClientSettings.load();
		loginHandler = new LoginHandler();
	}
	
	@Override
	public void show() {
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		Skin skin = skinManager.getDefaultSkin();
		
		name = new TextField(clientSettings.getUsername(), skin);
		name.setMaxLength(25);
		
		password = new TextField(clientSettings.getPassword(), skin);
		password.setPasswordMode(true);
		password.setPasswordCharacter('*'); // the skin does not contain bullet characters
		
		host = new TextField(clientSettings.getHost(), skin);
		
		port = new TextField(Integer.toString(clientSettings.getPort()), skin);
		port.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		port.setMaxLength(5);
		
		TextButton login = new TextButton("Login", skin);
		TextButton signUp = new TextButton("Sign Up", skin);
		
		Label labelName = new Label("Username", skin);
		Label labelPassword = new Label("Password", skin);
		Label labelHost = new Label("Host URL", skin);
		Label labelPort = new Label("Port", skin);
		
		error = new Label("", skin);
		error.getStyle().fontColor = Color.RED;
		error.setWrap(true);
		
		table.columnDefaults(0).minWidth(100);
		table.columnDefaults(1).minWidth(250).align(Align.left).fillX();
		table.columnDefaults(2).minWidth(100).align(Align.left).fillX();
		table.columnDefaults(3).minWidth(100);
		table.add();
		table.add(labelName).colspan(2);
		table.add();
		
		table.row();
		table.add();
		table.add(name).colspan(2);
		table.add();
		
		table.row().padTop(10);
		table.add();
		table.add(labelPassword).colspan(2);
		table.add();
		
		table.row();
		table.add();
		table.add(password).colspan(2);
		table.add();
		
		table.row().padTop(10);
		table.add();
		table.add(labelHost);
		table.add(labelPort).pad(10, 10, 0, 0);
		table.add();
		
		table.row();
		table.add();
		table.add(host);
		table.add(port).pad(0, 10, 0, 0);
		table.add();
		
		table.row().padTop(10);
		table.add();
		Table subTableButtons = new Table();
		subTableButtons.add(login).width(200);
		subTableButtons.add(signUp).width(200);
		table.add(subTableButtons).colspan(2);
		table.add();
		
		table.row().padTop(20);
		table.add(error).colspan(4).width(Value.percentHeight(0.7f)).center();
		
		login.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				login();
			}
		});
		
		signUp.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				signUp();
			}
		});
	}
	
	@Override
	public void render(float delta) {
		super.render(delta); // draw the stage
		if (changeToMainMenuScreen) {
			changeToMainMenuScreen = false;
			screenChanger.setScreen(new MainMenuScreen());
		}
	}
	
	private void login() {
		String username = name.getText();
		String pwd = password.getText();
		String hostUrl = host.getText();
		String hostPort = port.getText();
		
		Gdx.app.log(getClass().getSimpleName(), "logging in to server " + hostUrl + ":" + port + " with user name: " + username);
		validateInputAndExecute(username, pwd, hostUrl, hostPort, () -> {
			try {
				loginHandler.login(username, pwd, hostUrl, Integer.parseInt(hostPort), this::changeToMainMenuScreen);
				dispose();
			}
			catch (LoginException e) {
				error.setText(e.getMessage());
			}
		});
	}
	
	private void signUp() {
		String username = name.getText();
		String pwd = password.getText();
		String hostUrl = host.getText();
		String hostPort = port.getText();
		
		Gdx.app.log(getClass().getSimpleName(), "signing up to server " + hostUrl + ":" + port + " with user name: " + username);
		validateInputAndExecute(username, pwd, hostUrl, hostPort, () -> {
			try {
				loginHandler.signUp(username, pwd, hostUrl, Integer.parseInt(hostPort), this::changeToMainMenuScreen);
				dispose();
			}
			catch (LoginException e) {
				error.setText(e.getMessage());
			}
		});
	}
	
	private void validateInputAndExecute(String username, String pwd, String hostUrl, String hostPort, Runnable execute) {
		if (username.isEmpty()) {
			error.setText("Cannot login with empty name. Please choose a username to login.");
		}
		else if (pwd.isEmpty()) {
			error.setText("Please enter the password.");
		}
		else if (hostUrl.isEmpty() || hostPort.isEmpty()) {
			error.setText("You must choose a host and a port to login to a server.");
		}
		else {
			error.setText("");
			execute.run();
		}
	}
	
	private void changeToMainMenuScreen() {
		changeToMainMenuScreen = true;
	}
}
