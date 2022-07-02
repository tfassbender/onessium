package net.jfabricationgames.onnessium.util;

import java.io.IOException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.exception.CdiException;

public class TestUtils {
	
	private TestUtils() {}
	
	public static void createCdiContainer() throws CdiException, IOException {
		CdiContainer.create("net.jfabricationgames.onnessium");
	}
	
	/**
	 * Mock the Gdx.app variable, so the logging won't lead to an NPE.
	 */
	public static void mockGdxApplication() {
		Gdx.app = new Application() {
			
			@Override
			public ApplicationListener getApplicationListener() {
				return null;
			}
			
			@Override
			public Graphics getGraphics() {
				return null;
			}
			
			@Override
			public Audio getAudio() {
				return null;
			}
			
			@Override
			public Input getInput() {
				return null;
			}
			
			@Override
			public Files getFiles() {
				return null;
			}
			
			@Override
			public Net getNet() {
				return null;
			}
			
			@Override
			public void log(String tag, String message) {}
			
			@Override
			public void log(String tag, String message, Throwable exception) {}
			
			@Override
			public void error(String tag, String message) {}
			
			@Override
			public void error(String tag, String message, Throwable exception) {}
			
			@Override
			public void debug(String tag, String message) {}
			
			@Override
			public void debug(String tag, String message, Throwable exception) {}
			
			@Override
			public void setLogLevel(int logLevel) {}
			
			@Override
			public int getLogLevel() {
				return 0;
			}
			
			@Override
			public void setApplicationLogger(ApplicationLogger applicationLogger) {}
			
			@Override
			public ApplicationLogger getApplicationLogger() {
				return null;
			}
			
			@Override
			public ApplicationType getType() {
				return null;
			}
			
			@Override
			public int getVersion() {
				return 0;
			}
			
			@Override
			public long getJavaHeap() {
				return 0;
			}
			
			@Override
			public long getNativeHeap() {
				return 0;
			}
			
			@Override
			public Preferences getPreferences(String name) {
				return null;
			}
			
			@Override
			public Clipboard getClipboard() {
				return null;
			}
			
			@Override
			public void postRunnable(Runnable runnable) {}
			
			@Override
			public void exit() {}
			
			@Override
			public void addLifecycleListener(LifecycleListener listener) {}
			
			@Override
			public void removeLifecycleListener(LifecycleListener listener) {}
		};
	}
}
