package net.jfabricationgames.onnessium.user.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import net.jfabricationgames.onnessium.network.shared.Network;
import net.jfabricationgames.onnessium.util.TestUtils;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class LastUsedClientSettingsTest {
	
	public static final String TEMPORARY_SETTINGS_FILE_PATH = LastUsedClientSettings.LAST_USED_CLIENT_SETTINGS_DIRECTORY_PATH + "/test_settings.properties";
	public static final String NOT_EXISTING_SETTINGS_FILE_PATH = LastUsedClientSettings.LAST_USED_CLIENT_SETTINGS_DIRECTORY_PATH + "/not_existing_settings.properties";
	
	@BeforeAll
	public static void moveExistingConfigFile() throws NoSuchFieldException, IllegalAccessException {
		LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(TEMPORARY_SETTINGS_FILE_PATH);
		TestUtils.mockGdxApplication();
	}
	
	@AfterAll
	public static void restoreExistingConfigFile() throws NoSuchFieldException, IllegalAccessException {
		LastUsedClientSettingsTestUtil.resetSettingsPropertyPath();
		new File(TEMPORARY_SETTINGS_FILE_PATH).delete();
	}
	
	/**
	 * Must be run as first, or in debug mode to succeed... probably because some files cannot be deleted
	 */
	@Test
	public void test01_LoadDefaultProperties() throws NoSuchFieldException, IllegalAccessException {
		// change the path to a not existing path, to test loading the default settings (because deleting the existing file may fail)
		LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(NOT_EXISTING_SETTINGS_FILE_PATH);
		
		LastUsedClientSettings defaultSettings = null;
		try {
			defaultSettings = LastUsedClientSettings.load();
		}
		finally {
			// change the path back to the usual test path
			LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(TEMPORARY_SETTINGS_FILE_PATH);
		}
		
		assertEquals("Arthur Dent", defaultSettings.getUsername());
		assertEquals("", defaultSettings.getPassword());
		assertEquals(Network.DEFAULT_HOST, defaultSettings.getHost());
		assertEquals(Network.DEFAULT_PORT, defaultSettings.getPort());
	}
	
	@Test
	public void test02_StoreAndLoadProperties() {
		String username = "Trillian";
		String password = "secure_password_1!";
		String host = "jfabricationgames.ddns.net";
		int port = 42;
		
		LastUsedClientSettings.store(username, password, host, port);
		LastUsedClientSettings settings = LastUsedClientSettings.load();
		
		assertEquals(username, settings.getUsername());
		assertEquals(password, settings.getPassword());
		assertEquals(host, settings.getHost());
		assertEquals(port, settings.getPort());
	}
}
