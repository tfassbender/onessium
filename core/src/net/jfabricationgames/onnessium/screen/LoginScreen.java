package net.jfabricationgames.onnessium.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.input.InputManager;
import net.jfabricationgames.onnessium.network.client.NetworkClient;

public class LoginScreen extends ScreenAdapter {
	
	@Inject
	private InputManager inputManager;
	@Inject
	private SkinManager skinManager;
	@Inject
	private NetworkClient networkClient;
	
	private Stage stage;
	
	private TextField textFieldName;
	private Label labelError;
	
	public LoginScreen() {
		CdiContainer.injectTo(this);
		
		stage = new Stage(new ScreenViewport());
		inputManager.addInputProcessor(stage);
	}
	
	@Override
	public void show() {
		Table table = new Table();
		table.setFillParent(true);
		//table.setDebug(true);
		stage.addActor(table);
		
		Skin skin = skinManager.getDefaultSkin();
		
		textFieldName = new TextField("", skin);
		TextButton buttonLogin = new TextButton("Login", skin);
		labelError = new Label("", skin);
		labelError.getStyle().fontColor = Color.RED;
		
		table.columnDefaults(0).minWidth(350);
		table.add(textFieldName);
		table.row().padTop(10);
		table.add(buttonLogin);
		table.row().padTop(20);
		table.add(labelError).width(Value.percentHeight(0.7f));
		labelError.setWrap(true);
		
		buttonLogin.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				login();
			}
		});
	}
	
	private void login() {
		String username = textFieldName.getText();
		Gdx.app.log(getClass().getSimpleName(), "logging in with user name: " + username);
		if (textFieldName.getText().isEmpty()) {
			labelError.setText("Cannot login with empty name. Please choose a username to login.");
		}
		else {
			labelError.setText("");
			networkClient.connect(username, "password_1", "localhost", 4711); //TODO remove after tests
			dispose();
		}
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0f, 0f, 0f, 1f);
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void dispose() {
		inputManager.removeInputProcessor(stage);
		stage.dispose();
	}
}
