package net.jfabricationgames.onnessium.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
		try {
			CdiContainer.create("net.jfabricationgames.onnessium");
		}
		catch (CdiException e) {
			// the CDI container might have already been initialised
		}
	}
	
	public static void setFieldPerReflection(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = instance.getClass().getDeclaredField(fieldName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		field.set(instance, value);
		field.setAccessible(accessible);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldPerReflection(Object instance, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = instance.getClass().getDeclaredField(fieldName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		Object value = field.get(instance);
		field.setAccessible(accessible);
		
		return (T) value;
	}
	
	public static void invokePrivateMethod(Object instance, String methodName, Object... parameters) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = instance.getClass().getDeclaredMethod(methodName);
		boolean accessible = method.isAccessible();
		method.setAccessible(true);
		method.invoke(instance, parameters);
		method.setAccessible(accessible);
	}
	
	// see: https://stackoverflow.com/a/3301720/8178842
	public static void setStaticFinalFieldPerReflection(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);
		
		// ignore that the field may be private
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);
		
		// ignore that the field may be final
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		boolean isFinal = (modifiersField.getModifiers() & Modifier.FINAL) != 0;
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		modifiersField.setAccessible(false);
		
		field.set(null, value);
		
		field.setAccessible(isAccessible);
		if (isFinal) {
			field.setInt(field, field.getModifiers() & Modifier.FINAL);
		}
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
