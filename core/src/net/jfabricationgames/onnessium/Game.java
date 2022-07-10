package net.jfabricationgames.onnessium;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.exception.CdiException;
import net.jfabricationgames.onnessium.screen.LoginScreen;
import net.jfabricationgames.onnessium.screen.ScreenChanger;

@ApplicationScoped
public class Game extends com.badlogic.gdx.Game implements ScreenChanger {
	
	private static final Logger log = LoggerFactory.getLogger(Game.class);
	
	private static Game instance;
	
	private Runnable preGameConfigurator;
	
	public static synchronized Game createInstance(Runnable preGameConfigurator) {
		if (instance == null) {
			instance = new Game(preGameConfigurator);
		}
		return instance;
	}
	
	public static Game getInstance() {
		return instance;
	}
	
	private Game(Runnable preGameConfigurator) {
		this.preGameConfigurator = preGameConfigurator;
	}
	
	@Override
	public void create() {
		preGameConfigurator.run();
		initializeCdiContainer();
		NetworkDtoRegistry.initializeNetworkClasses();
		ClientHandlerRegistry.initializeClientHandlers();
		
		/*
		 * Do not call this from the constructor, or it will cause an UnsatisfiedLinkError when 
		 * creating a SpriteBatch or a Stage in LoginScreen.
		 */
		setScreen(new LoginScreen());
	}
	
	private void initializeCdiContainer() {
		try {
			CdiContainer.create("net.jfabricationgames.onnessium");
		}
		catch (CdiException | IOException e) {
			log.error("Could not create CDI container", e);
			Gdx.app.exit();
		}
	}
}
