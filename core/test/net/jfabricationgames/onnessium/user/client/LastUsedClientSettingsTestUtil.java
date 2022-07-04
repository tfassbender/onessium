package net.jfabricationgames.onnessium.user.client;

import net.jfabricationgames.onnessium.util.TestUtils;

public class LastUsedClientSettingsTestUtil {
	
	public static void setSettingsPropertyPathTo(String path) throws NoSuchFieldException, IllegalAccessException {
		TestUtils.setStaticFinalFieldPerReflection(LastUsedClientSettings.class, "LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH", path);
	}
	
	public static void resetSettingsPropertyPath() throws NoSuchFieldException, IllegalAccessException {
		LastUsedClientSettingsTestUtil.setSettingsPropertyPathTo(LastUsedClientSettings.LAST_USED_CLIENT_SETTINGS_PROPERTY_PATH);
	}
}
