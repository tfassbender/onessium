package net.jfabricationgames.onnessium.archunit.util;

public class ArchUnitUtils {
	
	public static final String TOP_LEVEL_PACKAGE = "net.jfabricationgames.onnessium";
	public static final String PROJECT_PACKAGES = TOP_LEVEL_PACKAGE + "..";
	public static final String LIBRARY_JAVA = "java..";
	public static final String LIBRARY_LIBGDX = "com.badlogic.gdx..";
	public static final String LIBRARY_JFG_CDI = "net.jfabricationgames.cdi..";
	
	private ArchUnitUtils() {}
	
	public static String[] addDefaultLibraryPackages(String... packages) {
		String[] allPackages = new String[packages.length + 3];
		System.arraycopy(packages, 0, allPackages, 0, packages.length);
		
		allPackages[packages.length] = LIBRARY_JAVA;
		allPackages[packages.length + 1] = LIBRARY_LIBGDX;
		allPackages[packages.length + 2] = LIBRARY_JFG_CDI;
		
		return allPackages;
	}
}
