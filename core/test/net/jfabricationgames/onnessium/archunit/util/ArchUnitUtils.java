package net.jfabricationgames.onnessium.archunit.util;

public class ArchUnitUtils {
	
	private ArchUnitUtils() {}
	
	public static String[] addDefaultLibraryPackages(String... packages) {
		String[] allPackages = new String[packages.length + 3];
		System.arraycopy(packages, 0, allPackages, 0, packages.length);
		
		allPackages[packages.length] = PackageConstants.LIBRARY_JAVA;
		allPackages[packages.length + 1] = PackageConstants.LIBRARY_LIBGDX;
		allPackages[packages.length + 2] = PackageConstants.LIBRARY_JFG_CDI;
		
		return allPackages;
	}
}
