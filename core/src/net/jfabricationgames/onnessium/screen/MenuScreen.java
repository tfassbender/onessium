package net.jfabricationgames.onnessium.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.onnessium.input.InputManager;

public abstract class MenuScreen extends ScreenAdapter {
	
	@Inject
	protected InputManager inputManager;
	@Inject
	protected SkinManager skinManager;
	@Inject
	protected ScreenChanger screenChanger;
	
	protected Stage stage;
	
	public MenuScreen() {
		CdiContainer.injectTo(this);
		
		stage = new Stage(new ScreenViewport());
		inputManager.addInputProcessor(stage);
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
