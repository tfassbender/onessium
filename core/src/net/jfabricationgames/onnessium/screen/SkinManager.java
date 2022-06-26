package net.jfabricationgames.onnessium.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class SkinManager {
	
	private Skin defaultSkin;
	
	public SkinManager() {
		defaultSkin = new Skin(Gdx.files.internal("skin/star_soldier_ui/star-soldier-ui.json"));
	}
	
	public Skin getDefaultSkin() {
		return defaultSkin;
	}
}
