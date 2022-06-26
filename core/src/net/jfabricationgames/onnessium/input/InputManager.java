package net.jfabricationgames.onnessium.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class InputManager {
	
	private InputMultiplexer inputMultiplexer;
	
	public InputManager() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	public void addInputProcessor(InputProcessor processor) {
		inputMultiplexer.addProcessor(processor);
	}
	
	public void removeInputProcessor(InputProcessor processor) {
		inputMultiplexer.removeProcessor(processor);
	}
}
